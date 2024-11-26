package mod.chiselsandbits.helpers;

import java.util.Arrays;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TestInventory implements Container {
    private Inventory logicBase;
    private ItemStack[] slots;

    public TestInventory(Inventory original) {
        logicBase = original;
        slots = new ItemStack[original.getContainerSize()];

        for (int x = 0; x < slots.length; ++x) {
            slots[x] = original.getItem(x);

            if (slots[x] != null) {
                slots[x] = slots[x].copy();
            }
        }
    }

    @Override
    public int getContainerSize() {
        return slots.length;
    }

    @Override
    public boolean isEmpty() {
        return Arrays.stream(slots).allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return slots[i];
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        if (slots[i] != null) {
            if (ModUtil.getStackSize(slots[i]) <= j) {
                return removeItemNoUpdate(i);
            } else {
                return slots[i].split(j);
            }
        }

        return ModUtil.getEmptyStack();
    }

    @Override
    public int getMaxStackSize() {
        return logicBase.getMaxStackSize() == -1 ? Container.super.getMaxStackSize() : logicBase.getMaxStackSize();
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        final ItemStack r = slots[i];
        slots[i] = ModUtil.getEmptyStack();
        return r;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        slots[i] = itemStack;
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return logicBase.canPlaceItem(i, itemStack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int x = 0; x < slots.length; ++x) {
            slots[x] = ModUtil.getEmptyStack();
        }
    }
}
