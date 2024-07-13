package mod.chiselsandbits.crafting;

import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ChiselCrafting extends CustomRecipe {

    public ChiselCrafting(CraftingBookCategory name) {
        super(name);
    }

    /**
     * Find the bag and pattern...
     *
     * @param inv
     * @return
     */
    private ChiselCraftingRequirements getCraftingReqs(final CraftingContainer inv, final boolean copy) {
        ItemStack pattern = null;

        for (int x = 0; x < inv.getContainerSize(); x++) {
            final ItemStack is = inv.getItem(x);

            if (is == null) {
                continue;
            }

            if (is.getItem() == ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get() && pattern == null) {
                pattern = is;
            } else if (is.getItem() instanceof ItemBitBag) {
                continue;
            } else if (is.getItem() instanceof ItemChiseledBit) {
                continue;
            } else if (!ModUtil.isEmpty(is)) {
                return null;
            }
        }

        if (pattern == null || pattern.hasTag() == false) {
            return null;
        }

        final ChiselCraftingRequirements r = new ChiselCraftingRequirements(inv, pattern, copy);
        if (r.isValid()) {
            return r;
        }

        return null;
    }

    @Override
    public boolean matches(final CraftingContainer inv, final Level worldIn) {
        return getCraftingReqs(inv, true) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        final ChiselCraftingRequirements req = getCraftingReqs(inv, true);

        if (req != null) {
            return ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get().getPatternedItem(req.pattern, true);
        }

        return ModUtil.getEmptyStack();
    }

    @Override
    public boolean canCraftInDimensions(final int width, final int height) {
        return width * height > 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ModUtil.getEmptyStack();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer inv) {
        final NonNullList<ItemStack> out = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        // just getting this will alter the stacks..
        final ChiselCraftingRequirements r = getCraftingReqs(inv, false);

        if (inv.getContainerSize() != r.pile.length) {
            throw new RuntimeException("Inventory Changed Size!");
        }

        for (int x = 0; x < r.pile.length; x++) {

            if (r.pile[x] != null && ModUtil.getStackSize(r.pile[x]) > 0) {
                out.set(x, r.pile[x]);
            }
        }

        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CHISEL_CRAFTING.get();
    }
}
