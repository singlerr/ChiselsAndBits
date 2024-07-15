package mod.chiselsandbits.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import mod.chiselsandbits.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static RandomSource RENDER_RANDOM = RandomSource.create();

    public static void drawSelectionBoundingBoxIfExists(
            final PoseStack matrixStack,
            final AABB bb,
            final BlockPos blockPos,
            final Player player,
            final float partialTicks,
            final boolean NormalBoundingBox) {
        drawSelectionBoundingBoxIfExistsWithColor(
                matrixStack, bb, blockPos, player, partialTicks, NormalBoundingBox, 0, 0, 0, 102, 32);
    }

    public static void drawSelectionBoundingBoxIfExistsWithColor(
            final PoseStack matrixStack,
            final AABB bb,
            final BlockPos blockPos,
            final Player player,
            final float partialTicks,
            final boolean NormalBoundingBox,
            final int red,
            final int green,
            final int blue,
            final int alpha,
            final int seeThruAlpha) {
        if (bb != null) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            //            GL11.glLineWidth(2.0F);
            //            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);

            if (!NormalBoundingBox) {
                RenderHelper.renderBoundingBox(
                        matrixStack,
                        bb.expandTowards(0.002D, 0.002D, 0.002D)
                                .move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                        red,
                        green,
                        blue,
                        alpha);
            }

            RenderSystem.disableDepthTest();

            RenderHelper.renderBoundingBox(
                    matrixStack,
                    bb.expandTowards(0.002D, 0.002D, 0.002D).move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                    red,
                    green,
                    blue,
                    seeThruAlpha);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            //            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    public static void drawLineWithColor(
            final PoseStack matrixStack,
            final Vec3 a,
            final Vec3 b,
            final BlockPos blockPos,
            final Player player,
            final float partialTicks,
            final boolean NormalBoundingBox,
            final int red,
            final int green,
            final int blue,
            final int alpha,
            final int seeThruAlpha) {
        if (a != null && b != null) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            //            GL11.glLineWidth(2.0F);
            //            RenderSystem.disableTexture();
            RenderSystem.depthMask(false);
            //            RenderSystem.shadeModel(GL11.GL_FLAT);

            final Vec3 a2 = a.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            final Vec3 b2 = b.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (!NormalBoundingBox) {
                RenderHelper.renderLine(matrixStack, a2, b2, red, green, blue, alpha);
            }

            RenderSystem.disableDepthTest();

            RenderHelper.renderLine(matrixStack, a2, b2, red, green, blue, seeThruAlpha);

            //            RenderSystem.shadeModel(Minecraft.useAmbientOcclusion() ? GL11.GL_SMOOTH : GL11.GL_FLAT);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            //            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    public static void renderQuads(
            final PoseStack matrixStack,
            final int alpha,
            final BufferBuilder renderer,
            final List<BakedQuad> quads,
            final Level worldObj,
            final BlockPos blockPos,
            int combinedLightIn,
            int combinedOverlayIn) {
        int i = 0;
        for (final int j = quads.size(); i < j; ++i) {
            final BakedQuad bakedquad = quads.get(i);
            final int color = bakedquad.getTintIndex() == -1
                    ? alpha | 0xffffff
                    : getTint(alpha, bakedquad.getTintIndex(), worldObj, blockPos);

            float cb = color & 0xFF;
            float cg = (color >>> 8) & 0xFF;
            float cr = (color >>> 16) & 0xFF;
            float ca = (color >>> 24) & 0xFF;

            renderer.putBulkData(
                    matrixStack.last(),
                    bakedquad,
                    new float[] {ca, ca, ca, ca},
                    cb,
                    cg,
                    cr,
                    new int[] {combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn},
                    combinedOverlayIn,
                    false);
        }
    }

    // Custom replacement of 1.9.4 -> 1.10's method that changed.
    public static void renderBoundingBox(
            final PoseStack matrixStack,
            final AABB boundingBox,
            final int red,
            final int green,
            final int blue,
            final int alpha) {
        //        GL11.glPushAttrib(8256);
        final Tesselator tess = Tesselator.getInstance();
        final BufferBuilder bufferBuilder = tess.getBuilder();
        //        RenderSystem.shadeModel(GL11.GL_FLAT);
        bufferBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        final float minX = (float) boundingBox.minX;
        final float minY = (float) boundingBox.minY;
        final float minZ = (float) boundingBox.minZ;
        final float maxX = (float) boundingBox.maxX;
        final float maxY = (float) boundingBox.maxY;
        final float maxZ = (float) boundingBox.maxZ;

        // lower ring ( starts to 0 / 0 )
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, minY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, minY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, minY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, minY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, minY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();

        // Y line at 0 / 0
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, maxY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();

        // upper ring ( including previous point to draw 4 lines )
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, maxY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, maxY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, maxY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, maxY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();

        /*
         * the next 3 Y Lines use flat shading to render invisible lines to
         * enable doing this all in one pass.
         */

        // Y line at 1 / 0
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, minY, minZ)
                .color(red, green, blue, 0)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, maxY, minZ)
                .color(red, green, blue, alpha)
                .endVertex();

        // Y line at 0 / 1
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, minY, maxZ)
                .color(red, green, blue, 0)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), minX, maxY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();

        // Y line at 1 / 1
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, minY, maxZ)
                .color(red, green, blue, 0)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), maxX, maxY, maxZ)
                .color(red, green, blue, alpha)
                .endVertex();

        tess.end();
    }

    public static void renderLine(
            final PoseStack matrixStack,
            final Vec3 a,
            final Vec3 b,
            final int red,
            final int green,
            final int blue,
            final int alpha) {
        final Tesselator tess = Tesselator.getInstance();
        final BufferBuilder bufferBuilder = tess.getBuilder();
        //        RenderSystem.shadeModel(GL11.GL_FLAT);
        bufferBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder
                .vertex(matrixStack.last().pose(), (float) a.x, (float) a.y, (float) a.z)
                .color(red, green, blue, alpha)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), (float) b.x, (float) b.y, (float) b.z)
                .color(red, green, blue, alpha)
                .endVertex();
        tess.end();
    }

    public static int getTint(final int alpha, final int tintIndex, final Level worldObj, final BlockPos blockPos) {
        return alpha
                | Minecraft.getInstance()
                        .getBlockColors()
                        .getColor(ModBlocks.getChiseledDefaultState(), worldObj, blockPos, tintIndex);
    }

    public static void renderModel(
            final PoseStack matrixStack,
            final BakedModel model,
            final Level worldObj,
            final BlockPos blockPos,
            final int alpha,
            final int combinedLightmap,
            final int combinedOverlay) {
        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

        for (final Direction enumfacing : Direction.values()) {
            renderQuads(
                    matrixStack,
                    alpha,
                    buffer,
                    model.getQuads(null, enumfacing, RENDER_RANDOM),
                    worldObj,
                    blockPos,
                    combinedLightmap,
                    combinedOverlay);
        }

        renderQuads(
                matrixStack,
                alpha,
                buffer,
                model.getQuads(null, null, RENDER_RANDOM),
                worldObj,
                blockPos,
                combinedLightmap,
                combinedOverlay);
        tessellator.end();
    }

    public static void renderGhostModel(
            final PoseStack matrixStack,
            final BakedModel baked,
            final Level worldObj,
            final BlockPos blockPos,
            final boolean isUnplaceable,
            final int combinedLightmap,
            final int combinedOverlay) {
        final int alpha = isUnplaceable ? 0x22000000 : 0xaa000000;
        Minecraft.getInstance().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);
        //        guiGraphics.setColor(1,1,1,1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.colorMask(false, false, false, false);

        RenderHelper.renderModel(matrixStack, baked, worldObj, blockPos, alpha, combinedLightmap, combinedOverlay);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderHelper.renderModel(matrixStack, baked, worldObj, blockPos, alpha, combinedLightmap, combinedOverlay);

        RenderSystem.disableBlend();
    }
}
