package mod.chiselsandbits.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import java.util.WeakHashMap;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Disable breaking blocks when using a chisel / bit, some items break too fast
 * for the other code to prevent which is where this comes in.
 * <p>
 * This manages survival chisel actions, creative some how skips this and calls
 * onBlockStartBreak on its own, but when in creative this is called on the
 * server... which still needs to be canceled or it will break the block.
 * <p>
 * The whole things, is very strange.
 */
public class EventPlayerInteract {

    private static WeakHashMap<Player, Boolean> serverSuppressEvent = new WeakHashMap<Player, Boolean>();

    public static void register() {
        PlayerInteractionEvents.LEFT_CLICK_BLOCK.register(EventPlayerInteract::interaction);
        UseBlockCallback.EVENT.register(EventPlayerInteract::interaction);
    }

    public static void setPlayerSuppressionState(final Player player, final boolean state) {
        if (state) {
            serverSuppressEvent.put(player, state);
        } else {
            serverSuppressEvent.remove(player);
        }
    }

    private static void interaction(final PlayerInteractionEvents.LeftClickBlock event) {
        if (event.getPlayer() != null && event.getUseItem() == BaseEvent.Result.DENY) {
            final ItemStack is = event.getItemStack();
            final boolean validEvent = event.getPos() != null && event.getLevel() != null;
            if ((is.getItem() instanceof ItemChisel || is.getItem() instanceof ItemChiseledBit) && validEvent) {
                final BlockState state = event.getLevel().getBlockState(event.getPos());
                if (BlockBitInfo.canChisel(state)) {
                    if (event.getLevel().isClientSide) {
                        // this is called when the player is survival -
                        // client side.
                        is.getItem().onBlockStartBreak(is, event.getPos(), event.getPlayer());
                    }

                    // cancel interactions vs chiseable blocks, creative is
                    // magic.
                    event.setCanceled(true);
                }
            }
        }

        testInteractionSupression(event, event.getUseItem());
    }

    private static InteractionResult interaction(
            Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {

        return testInteractionSupression(player, world, hand, hitResult);
    }

    private static InteractionResult testInteractionSupression(
            Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        // client is dragging...
        if (world.isClientSide) {
            if (ClientSide.instance.getStartPos() != null) {
                return InteractionResult.FAIL;
            }
        }
        ItemStack itemEntity = player.getItemInHand(hand);
        // server is supressed.
        if (!world.isClientSide && itemEntity != null) {
            if (serverSuppressEvent.containsKey(player)) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private static void testInteractionSupression(final PlayerInteractionEvents event, final BaseEvent.Result useItem) {
        // client is dragging...
        if (event.getLevel().isClientSide) {
            if (ClientSide.instance.getStartPos() != null) {
                event.setCanceled(true);
            }
        }

        // server is supressed.
        if (!event.getLevel().isClientSide && event.getEntity() != null && useItem != BaseEvent.Result.DENY) {
            if (serverSuppressEvent.containsKey(event.getPlayer())) {
                event.setCanceled(true);
            }
        }
    }
}
