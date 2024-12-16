package mod.chiselsandbits.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.OptionalDouble;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class RenderHelper {

    private static final RenderType CHISEL_PREVIEW = previewRender();
    private static final RenderType.CompositeRenderType BOUNDING_BOX_LINES = RenderType.create(
            "lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1.1)))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                    .setCullState(RenderType.NO_CULL)
                    .createCompositeState(false));
    public static RandomSource RENDER_RANDOM = RandomSource.create();

    private static RenderType previewRender() {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderType.LIGHTMAP)
                .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                .setWriteMaskState(RenderType.COLOR_WRITE)
                .setCullState(RenderType.NO_CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setOverlayState(RenderType.NO_OVERLAY)
                .createCompositeState(true);
        return RenderType.create(
                Constants.MOD_ID + ":chisels_preview",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                compositeState);
    }

    public static void drawSelectionBoundingBoxIfExists(
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
            final AABB bb,
            final BlockPos blockPos,
            final Player player,
            final float partialTicks,
            final boolean NormalBoundingBox) {
        drawSelectionBoundingBoxIfExistsWithColor(
                matrixStack, buffers, bb, blockPos, player, partialTicks, NormalBoundingBox, 0, 0, 0, 102, 32);
    }

    public static void drawSelectionBoundingBoxIfExistsWithColor(
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
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
            if (!NormalBoundingBox) {
                RenderHelper.renderBoundingBox(
                        matrixStack,
                        buffers,
                        bb.expandTowards(0.002D, 0.002D, 0.002D)
                                .move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                        red,
                        green,
                        blue,
                        alpha);
            }

            RenderHelper.renderBoundingBox(
                    matrixStack,
                    buffers,
                    bb.expandTowards(0.002D, 0.002D, 0.002D).move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                    red,
                    green,
                    blue,
                    seeThruAlpha);
        }
    }

    public static void drawLineWithColor(
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
            final Vec3 a,
            final Vec3 b,
            final BlockPos blockPos,
            final boolean NormalBoundingBox,
            final int red,
            final int green,
            final int blue,
            final int alpha,
            final int seeThruAlpha) {
        if (a != null && b != null) {
            final Vec3 a2 = a.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            final Vec3 b2 = b.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (!NormalBoundingBox) {
                RenderHelper.renderLine(matrixStack, buffers, a2, b2, red, green, blue, alpha);
            }

            RenderHelper.renderLine(matrixStack, buffers, a2, b2, red, green, blue, seeThruAlpha);
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
            int color = bakedquad.getTintIndex() == -1
                    ? alpha | 0xffffff
                    : getTint(alpha, bakedquad.getTintIndex(), worldObj, blockPos);

            float cb = (color & 0xFF) / 255f;
            float cg = ((color >>> 8) & 0xFF) / 255f;
            float cr = ((color >>> 16) & 0xFF) / 255f;
            float ca = ((alpha >>> 24) & 0xFF) / 255f;
            renderer.putBulkData(
                    matrixStack.last(),
                    bakedquad,
                    new float[] {ca, ca, ca, ca},
                    cr,
                    cg,
                    cb,
                    new int[] {combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn},
                    combinedOverlayIn,
                    false);
        }
    }

    public static void renderBoundingBox(
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
            final AABB boundingBox,
            final int red,
            final int green,
            final int blue,
            final int alpha) {
        LevelRenderer.renderVoxelShape(
                matrixStack,
                buffers.getBuffer(BOUNDING_BOX_LINES),
                Shapes.create(boundingBox),
                0,
                0,
                0,
                red,
                green,
                blue,
                alpha,
                true);
    }

    public static void renderLine(
            final PoseStack matrixStack,
            final MultiBufferSource buffers,
            final Vec3 a,
            final Vec3 b,
            final int red,
            final int green,
            final int blue,
            final int alpha) {
        final VertexConsumer bufferBuilder = buffers.getBuffer(RenderType.lines());
        bufferBuilder
                .vertex(matrixStack.last().pose(), (float) a.x, (float) a.y, (float) a.z)
                .color(red, green, blue, alpha)
                .normal(matrixStack.last().normal(), 1f, 1f, 1f)
                .endVertex();
        bufferBuilder
                .vertex(matrixStack.last().pose(), (float) b.x, (float) b.y, (float) b.z)
                .color(red, green, blue, alpha)
                .normal(matrixStack.last().normal(), 1f, 1f, 1f)
                .endVertex();
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

        RenderType renderType = CHISEL_PREVIEW;

        buffer.begin(renderType.mode(), renderType.format());

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

        renderType.end(buffer, RenderSystem.getVertexSorting());
    }

    public static void renderGhostModel(
            final PoseStack matrixStack,
            final BakedModel baked,
            final Level worldObj,
            final BlockPos blockPos,
            final boolean isUnplaceable,
            int combinedLightmap,
            final int combinedOverlay) {
        final int alpha = isUnplaceable ? 0x22000000 : 0xaa000000;
        RenderHelper.renderModel(matrixStack, baked, worldObj, blockPos, alpha, combinedLightmap, combinedOverlay);
    }
}
