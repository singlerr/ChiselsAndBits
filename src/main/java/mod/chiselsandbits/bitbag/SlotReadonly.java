package mod.chiselsandbits.bitbag;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotReadonly extends Slot {

  public SlotReadonly(final Container inventoryIn, final int index, final int xPosition,
                      final int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean mayPlace(final ItemStack stack) {
    return false;
  }

  @Override
  public boolean mayPickup(final Player playerIn) {
    return false;
  }
}
