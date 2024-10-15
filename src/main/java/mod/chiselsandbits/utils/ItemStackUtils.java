package mod.chiselsandbits.utils;

import net.minecraft.world.item.ItemStack;

public final class ItemStackUtils {

  private ItemStackUtils() {
  }

  public static ItemStack getContainerItem(ItemStack stack) {
    if (stack.getItem().hasCraftingRemainingItem()) {
      return new ItemStack(stack.getItem().getCraftingRemainingItem());
    }
    return ItemStack.EMPTY;
  }
}
