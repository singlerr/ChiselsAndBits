package mod.chiselsandbits.events;

import java.util.List;
import mod.chiselsandbits.events.extra.EntityItemPickupEvent;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EntityItemPickupEventHandler {

  public static void register() {
    EntityItemPickupEvent.EVENT.register(EntityItemPickupEventHandler::pickupItems);
  }

  private static boolean pickupItems(final ItemEntity entityItem, final Player player) {
    boolean modified = false;
    if (entityItem != null) {
      final ItemStack is = entityItem.getItem();
      if (is != null && is.getItem() instanceof ItemChiseledBit) {
        final int originalSize = ModUtil.getStackSize(is);
        final Container inv = player.inventory;
        final List<ItemBitBag.BagPos> bags = ItemBitBag.getBags(inv);

        // has the stack?
        final boolean seen = ModUtil.containsAtLeastOneOf(inv, is);

        if (seen) {
          for (final ItemBitBag.BagPos i : bags) {
            if (entityItem.isAlive()) {
              modified = updateEntity(
                  player,
                  entityItem,
                  i.inv.insertItem(ModUtil.nonNull(entityItem.getItem())),
                  originalSize)
                  || modified;
            }
          }
        } else {
          if (ModUtil.getStackSize(is) > is.getMaxStackSize() && entityItem.isAlive()) {
            final ItemStack singleStack = is.copy();
            ModUtil.setStackSize(singleStack, singleStack.getMaxStackSize());

            if (!player.inventory.add(singleStack)) {
              ModUtil.adjustStackSize(is,
                  -(singleStack.getMaxStackSize() - ModUtil.getStackSize(is)));
            }

            modified = updateEntity(player, entityItem, is, originalSize) || modified;
          } else {
            return false;
          }

          for (final ItemBitBag.BagPos i : bags) {

            if (entityItem.isAlive()) {
              modified = updateEntity(
                  player,
                  entityItem,
                  i.inv.insertItem(ModUtil.nonNull(entityItem.getItem())),
                  originalSize)
                  || modified;
            }
          }
        }
      }

      ItemBitBag.cleanupInventory(player, is);
    }

    return modified;
  }

  private static boolean updateEntity(
      final Player player, final ItemEntity ei, ItemStack is, final int originalSize) {
    if (is == null) {
      ei.remove(Entity.RemovalReason.DISCARDED);
      return true;
    } else {
      final int changed = ModUtil.getStackSize(is) - ModUtil.getStackSize(ei.getItem());
      ei.setItem(is);
      return changed != 0;
    }
  }
}
