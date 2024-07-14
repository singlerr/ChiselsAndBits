package mod.chiselsandbits.client;

import mod.chiselsandbits.registry.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ItemColorBitBag implements ItemColor {

    @Override
    public int getColor(final ItemStack p_getColor_1_, final int p_getColor_2_) {
        if (p_getColor_2_ == 1) {
            DyeColor color = ModItems.ITEM_BIT_BAG_DYED.get().getDyedColor(p_getColor_1_);
            if (color != null) return color.getTextColor();
        }

        return -1;
    }
}
