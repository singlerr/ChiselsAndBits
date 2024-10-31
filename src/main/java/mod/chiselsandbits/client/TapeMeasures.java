package mod.chiselsandbits.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.modes.IToolMode;
import mod.chiselsandbits.modes.TapeMeasureModes;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TapeMeasures {
    private static final double blockSize = 1.0;
    private static final double bitSize = 1.0 / 16.0;
    private static final double halfBit = bitSize / 2.0f;
    private final ArrayList<Measure> measures = new ArrayList<Measure>();

    private Measure preview;

    private static double AABBDistnace(final Vec3 eyes, final AABB box) {
        // snap eyes into the box...
        final double boxPointX = Math.min(box.maxX, Math.max(box.minX, eyes.x));
        final double boxPointY = Math.min(box.maxY, Math.max(box.minY, eyes.y));
        final double boxPointZ = Math.min(box.maxZ, Math.max(box.minZ, eyes.z));

        // then get the distance to it.
        return Math.sqrt(eyes.distanceToSqr(boxPointX, boxPointY, boxPointZ));
    }

    private static double getLineDistance(final Vec3 v, final Vec3 w, final Player player, final float partialTicks) {
        final Vec3 p = player.getEyePosition(partialTicks);
        final double segmentLength = v.distanceToSqr(w);

        if (segmentLength == 0.0) {
            return p.distanceTo(v);
        }

        final double t = Math.max(0, Math.min(1, p.subtract(v).dot(w.subtract(v)) / segmentLength));
        final Vec3 projection = v.add(w.subtract(v).scale(t));
        return p.distanceTo(projection);
    }

    public void clear() {
        measures.clear();
    }

    public void setPreviewMeasure(
            final BitLocation a, final BitLocation b, final IToolMode chMode, final ItemStack itemStack) {
        final Player player = ClientSide.instance.getPlayer();

        if (a == null || b == null) {
            preview = null;
        } else {
            preview = new Measure(a, b, chMode, getDimension(player), getColor(itemStack));
        }
    }

    public void addMeasure(
            final BitLocation a, final BitLocation b, final IToolMode chMode, final ItemStack itemStack) {
        final Player player = ClientSide.instance.getPlayer();

        while (measures.size() > 0
                && measures.size()
                        >= ChiselsAndBits.getConfig()
                                .getClient()
                                .maxTapeMeasures
                                .get()) {
            measures.remove(0);
        }

        final Measure newMeasure = new Measure(a, b, chMode, getDimension(player), getColor(itemStack));

        if (ChiselsAndBits.getConfig().getClient().displayMeasuringTapeInChat.get()) {
            final AABB box = newMeasure.getBoundingBox();

            final double LenX = box.maxX - box.minX;
            final double LenY = box.maxY - box.minY;
            final double LenZ = box.maxZ - box.minZ;
            final double Len = newMeasure.getVecA().distanceTo(newMeasure.getVecB());

            final String out = chMode == TapeMeasureModes.DISTANCE
                    ? getSize(Len)
                    : DeprecationHelper.translateToLocal(
                            "mod.chiselsandbits.tapemeasure.chatmsg", getSize(LenX), getSize(LenY), getSize(LenZ));

            final MutableComponent chatMsg = Component.literal(out);

            // NOT 100% Accurate, if anyone wants to try and resolve this, yay
            chatMsg.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(newMeasure.color.getTextColor())));

            player.sendSystemMessage(chatMsg);
        }

        measures.add(newMeasure);
    }

    private DyeColor getColor(final ItemStack itemStack) {
        return ModItems.ITEM_TAPE_MEASURE.get().getTapeColor(itemStack);
    }

    private ResourceLocation getDimension(final Player player) {
        return player.getCommandSenderWorld().dimension().registry();
    }

    public void render(final PoseStack matrixStack, final MultiBufferSource buffers, final float partialTicks) {
        if (!measures.isEmpty() || preview != null) {
            final Player player = ClientSide.instance.getPlayer();

            if (hasTapeMeasure(player.inventory)) {
                final ArrayList<Measure> sortList = new ArrayList<Measure>(measures.size() + 1);

                if (preview != null) {
                    preview.calcDistance(partialTicks);
                    sortList.add(preview);
                }

                for (final Measure m : measures) {
                    m.calcDistance(partialTicks);
                    sortList.add(m);
                }

                Collections.sort(sortList, new Comparator<Measure>() {

                    @Override
                    public int compare(final Measure a, final Measure b) {
                        return a.distance < b.distance ? 1 : a.distance > b.distance ? -1 : 0;
                    }
                });

                for (final Measure m : sortList) {
                    renderMeasure(m, m.distance, matrixStack, buffers, partialTicks);
                }
            }
        }
    }

    private boolean hasTapeMeasure(final Inventory inventory) {
        for (int x = 0; x < inventory.getContainerSize(); x++) {
            final ItemStack is = inventory.getItem(x);
            if (!is.isEmpty() && is.getItem() == ModItems.ITEM_TAPE_MEASURE.get()) {
                return true;
            }
        }

        return false;
    }

    private void renderMeasure(
            final Measure m,
            final double distance,
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
            final float partialTicks) {
        final Player player = ClientSide.instance.getPlayer();

        if (m.DimensionId != getDimension(player)) {
            return;
        }

        final int alpha = getAlphaFromRange(distance);
        if (alpha < 30) {
            return;
        }

        final int val = m.color.getTextColor();
        final int red = val >> 16 & 0xff;
        final int green = val >> 8 & 0xff;
        final int blue = val & 0xff;
        if (m.mode == TapeMeasureModes.DISTANCE) {
            final Vec3 a = m.getVecA();
            final Vec3 b = m.getVecB();

            RenderHelper.drawLineWithColor(
                    matrixStack, buffers, a, b, BlockPos.ZERO, false, red, green, blue, alpha, (int) (alpha / 3.4));

            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();

            final double Len = a.distanceTo(b) + bitSize;

            renderSize(
                    matrixStack,
                    player,
                    partialTicks,
                    (a.x + b.x) * 0.5,
                    (a.y + b.y) * 0.5,
                    (a.z + b.z) * 0.5,
                    Len,
                    red,
                    green,
                    blue);

            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            return;
        }

        final AABB box = m.getBoundingBox();
        RenderHelper.drawSelectionBoundingBoxIfExistsWithColor(
                matrixStack,
                buffers,
                box.expandTowards(-0.001, -0.001, -0.001),
                BlockPos.ZERO,
                player,
                partialTicks,
                false,
                red,
                green,
                blue,
                alpha,
                (int) (alpha / 3.4));

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        final double LenX = box.maxX - box.minX;
        final double LenY = box.maxY - box.minY;
        final double LenZ = box.maxZ - box.minZ;

        /**
         * TODO: Figure out some better logic for which lines to display the
         * numbers on.
         **/
        renderSize(
                matrixStack,
                player,
                partialTicks,
                box.minX,
                (box.maxY + box.minY) * 0.5,
                box.minZ,
                LenY,
                red,
                green,
                blue);
        renderSize(
                matrixStack,
                player,
                partialTicks,
                (box.minX + box.maxX) * 0.5,
                box.minY,
                box.minZ,
                LenX,
                red,
                green,
                blue);
        renderSize(
                matrixStack,
                player,
                partialTicks,
                box.minX,
                box.minY,
                (box.minZ + box.maxZ) * 0.5,
                LenZ,
                red,
                green,
                blue);

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    private int getAlphaFromRange(final double distance) {
        if (distance < 16) {
            return 102;
        }

        return (int) (102 - (distance - 16) * 6);
    }

    private void renderSize(
            final PoseStack matrixStack,
            final Player player,
            final float partialTicks,
            final double x,
            final double y,
            final double z,
            final double len,
            final int red,
            final int green,
            final int blue) {
        final double letterSize = 5.0;
        final double zScale = 0.001;

        final Font fontRenderer = Minecraft.getInstance().font;
        final String size = getSize(len);

        matrixStack.pushPose();
        matrixStack.translate(x, y + getScale(len) * letterSize, z);
        billBoard(matrixStack, player, partialTicks);
        matrixStack.scale(getScale(len), -getScale(len), (float) zScale);
        matrixStack.translate(-fontRenderer.width(size) * 0.5, 0, 0);
        RenderSystem.disableDepthTest();
        MultiBufferSource.BufferSource buffer =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        fontRenderer.drawInBatch(
                size,
                0,
                0,
                red << 16 | green << 8 | blue,
                true,
                matrixStack.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                15728880);
        buffer.endBatch();
        RenderSystem.enableDepthTest();
        matrixStack.popPose();
    }

    private float getScale(final double maxLen) {
        final double maxFontSize = 0.04;
        final double minFontSize = 0.004;

        final double delta = Math.min(1.0, maxLen / 4.0);
        double scale = maxFontSize * delta + minFontSize * (1.0 - delta);

        if (maxLen < 0.25) {
            scale = minFontSize;
        }

        return (float) Math.min(maxFontSize, scale);
    }

    private void billBoard(final PoseStack matrixStack, final Player player, final float partialTicks) {
        final Entity view = Minecraft.getInstance().cameraEntity;
        if (view != null) {
            final float yaw = view.yRotO + (view.yRot - view.yRotO) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
            final float pitch = view.xRotO + (view.xRot - view.xRotO) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        }
    }

    private String getSize(final double d) {
        final double blocks = Math.floor(d);
        final double bits = d - blocks;

        final StringBuilder b = new StringBuilder();

        if (blocks > 0) {
            b.append((int) blocks).append("m");
        }

        if (bits * 16 > 0.9999) {
            if (b.length() > 0) {
                b.append(" ");
            }
            b.append((int) (bits * 16)).append("b");
        }

        return b.toString();
    }

    private class Measure {
        public final IToolMode mode;
        public final BitLocation a;
        public final BitLocation b;
        public final DyeColor color;
        public final ResourceLocation DimensionId;
        public double distance = 1;

        public Measure(
                final BitLocation a2,
                final BitLocation b2,
                final IToolMode chMode,
                final ResourceLocation dimentionid,
                final DyeColor color) {
            a = a2;
            b = b2;
            mode = chMode;
            DimensionId = dimentionid;
            this.color = color;
        }

        public AABB getBoundingBox() {
            if (mode == TapeMeasureModes.BLOCK) {
                final double ax = a.blockPos.getX();
                final double ay = a.blockPos.getY();
                final double az = a.blockPos.getZ();
                final double bx = b.blockPos.getX();
                final double by = b.blockPos.getY();
                final double bz = b.blockPos.getZ();

                return new AABB(
                        Math.min(ax, bx),
                        Math.min(ay, by),
                        Math.min(az, bz),
                        Math.max(ax, bx) + blockSize,
                        Math.max(ay, by) + blockSize,
                        Math.max(az, bz) + blockSize);
            }

            final double ax = a.blockPos.getX() + bitSize * a.bitX;
            final double ay = a.blockPos.getY() + bitSize * a.bitY;
            final double az = a.blockPos.getZ() + bitSize * a.bitZ;
            final double bx = b.blockPos.getX() + bitSize * b.bitX;
            final double by = b.blockPos.getY() + bitSize * b.bitY;
            final double bz = b.blockPos.getZ() + bitSize * b.bitZ;

            return new AABB(
                    Math.min(ax, bx),
                    Math.min(ay, by),
                    Math.min(az, bz),
                    Math.max(ax, bx) + bitSize,
                    Math.max(ay, by) + bitSize,
                    Math.max(az, bz) + bitSize);
        }

        public Vec3 getVecA() {
            final double ax = a.blockPos.getX() + bitSize * a.bitX + halfBit;
            final double ay = a.blockPos.getY() + bitSize * a.bitY + halfBit;
            final double az = a.blockPos.getZ() + bitSize * a.bitZ + halfBit;
            return new Vec3(ax, ay, az);
        }

        public Vec3 getVecB() {
            final double bx = b.blockPos.getX() + bitSize * b.bitX + halfBit;
            final double by = b.blockPos.getY() + bitSize * b.bitY + halfBit;
            final double bz = b.blockPos.getZ() + bitSize * b.bitZ + halfBit;
            return new Vec3(bx, by, bz);
        }

        public void calcDistance(final float partialTicks) {
            if (mode == TapeMeasureModes.DISTANCE) {
                final Vec3 a = getVecA();
                final Vec3 b = getVecB();
                final Player player = ClientSide.instance.getPlayer();
                distance = getLineDistance(a, b, player, partialTicks);
            } else {
                final Player player = ClientSide.instance.getPlayer();
                final Vec3 eyes = player.getEyePosition(partialTicks);
                final AABB box = getBoundingBox();
                if (box.contains(eyes)) {
                    distance = 0.0;
                } else {
                    distance = AABBDistnace(eyes, box);
                }
            }
        }
    }
}
