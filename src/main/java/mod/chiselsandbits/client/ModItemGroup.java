package mod.chiselsandbits.client;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroup extends CreativeModeTab {

    public ModItemGroup() {
        super(ChiselsAndBits.MODID);
        setBackgroundSuffix("item_search.png");
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModItems.ITEM_BIT_BAG_DEFAULT.get());
    }
}
