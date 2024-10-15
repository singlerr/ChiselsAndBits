package mod.chiselsandbits.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mod.chiselsandbits.utils.ModClientHooks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionRenderDispatcher.RenderSection.RebuildTask.class)
public abstract class SectionRenderDispatcherMixin {

  @Inject(
      method = "compile",
      at =
      @At(
          value = "INVOKE",
          target =
              "Lnet/minecraft/client/renderer/SectionBufferBuilderPack;builder(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/BufferBuilder;",
          ordinal = 1))
  private void mod$setRenderType(
      float f,
      float g,
      float h,
      SectionBufferBuilderPack sectionBufferBuilderPack,
      CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir,
      @Local(ordinal = 0) RenderType renderType) {
    ModClientHooks.setRenderType(renderType);
  }

  @Inject(
      method = "compile",
      at =
      @At(
          value = "INVOKE",
          target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
          shift = At.Shift.AFTER))
  private void mod$clearRenderType(
      float f,
      float g,
      float h,
      SectionBufferBuilderPack sectionBufferBuilderPack,
      CallbackInfoReturnable<SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults> cir) {
    ModClientHooks.setRenderType(null);
  }
}
