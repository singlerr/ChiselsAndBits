package mod.chiselsandbits.mixin.compat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.events.extra.RenderWorldLastEvent;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
            method = "renderLevel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
                            shift = At.Shift.BEFORE))
    private void mod$dispatchRenderWorldLastEvent(float ticks, long l, PoseStack poseStack, CallbackInfo ci) {
        RenderWorldLastEvent.EVENT.invoker().handle(poseStack, ticks);
    }
}
