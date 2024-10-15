package mod.chiselsandbits.mixin;

import mod.chiselsandbits.events.extra.ResourceRegistrationEvent;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaintingTextureManager.class)
public abstract class PaintingTextureManagerMixin {

  @Inject(method = "<init>", at = @At("RETURN"))
  private void mod$invokeRegistrationEvent(TextureManager textureManager, CallbackInfo ci) {
    ResourceRegistrationEvent.EVENT.invoker().handle();
  }
}
