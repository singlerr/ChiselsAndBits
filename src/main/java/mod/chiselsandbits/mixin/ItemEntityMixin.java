package mod.chiselsandbits.mixin;

import mod.chiselsandbits.events.extra.EntityItemPickupEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(
            method = "playerTouch",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"),
            cancellable = true)
    private void mod$onItemPickup(Player player, CallbackInfo ci) {
        boolean result = EntityItemPickupEvent.EVENT.invoker().handle((ItemEntity) (Object) this, player);

        if (result) ci.cancel();
    }
}
