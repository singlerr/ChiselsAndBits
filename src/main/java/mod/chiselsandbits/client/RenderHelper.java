package mod.chiselsandbits.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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

public class RenderHelper {

  private static final RenderType CHISEL_PREVIEW = previewRender();
  public static RandomSource RENDER_RANDOM = RandomSource.create();

  private static RenderType previewRender() {
    RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
        .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
        .setTextureState(
            new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
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

      RenderHelper.renderBoundingBox(
          matrixStack,
          bb.expandTowards(0.002D, 0.002D, 0.002D)
              .move(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
          red,
          green,
          blue,
          seeThruAlpha);
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
      final Vec3 a2 = a.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      final Vec3 b2 = b.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      if (!NormalBoundingBox) {
        RenderHelper.renderLine(matrixStack, a2, b2, red, green, blue, alpha);
      }

      RenderHelper.renderLine(matrixStack, a2, b2, red, green, blue, seeThruAlpha);
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
      float ca = ((color >>> 24) & 0xFF) / 255f;
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

    RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
    RenderSystem.enableDepthTest();
    RenderSystem.lineWidth(5.5f);
    bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

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
    RenderSystem.disableDepthTest();
    RenderSystem.setShaderColor(1, 1, 1, 1);
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
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    RenderSystem.lineWidth(3.5f);
    bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
    bufferBuilder
        .vertex(matrixStack.last().pose(), (float) a.x, (float) a.y, (float) a.z)
        .color(red, green, blue, alpha)
        .endVertex();
    bufferBuilder
        .vertex(matrixStack.last().pose(), (float) b.x, (float) b.y, (float) b.z)
        .color(red, green, blue, alpha)
        .endVertex();
    tess.end();
    RenderSystem.setShaderColor(1, 1, 1, 1);
  }

  public static int getTint(final int alpha, final int tintIndex, final Level worldObj,
                            final BlockPos blockPos) {
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
      final int combinedLightmap,
      final int combinedOverlay) {

    final int alpha = isUnplaceable ? 0x22000000 : 0xaa000000;
    RenderHelper.renderModel(matrixStack, baked, worldObj, blockPos, alpha, combinedLightmap,
        combinedOverlay);
  }
}
