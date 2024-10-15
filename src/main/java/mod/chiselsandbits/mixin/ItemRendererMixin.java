package mod.chiselsandbits.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.utils.ModClientHooks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

  @Inject(method = "render", at = @At("HEAD"))
  private void mod$setRenderType(
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      boolean bl,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      int j,
      BakedModel bakedModel,
      CallbackInfo ci) {
    boolean bl3;
    if (itemDisplayContext != ItemDisplayContext.GUI
        && !itemDisplayContext.firstPerson()
        && itemStack.getItem() instanceof BlockItem) {
      Block block = ((BlockItem) itemStack.getItem()).getBlock();
      bl3 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
    } else {
      bl3 = true;
    }
    ModClientHooks.setRenderType(ItemBlockRenderTypes.getRenderType(itemStack, bl3));
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void mod$clearRenderType(
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      boolean bl,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      int j,
      BakedModel bakedModel,
      CallbackInfo ci) {
    ModClientHooks.setRenderType(null);
  }
}
