package mod.chiselsandbits.bitbag;

import mod.chiselsandbits.items.ItemBitBag;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BagCapabilityProvider extends BagStorage implements ICapabilityProvider {

    private final LazyOptional<IItemHandler> capResult = LazyOptional.of(() -> this);

    public BagCapabilityProvider(final ItemStack stack, final CompoundTag nbt) {
        this.stack = stack;
    }

    /**
     * Read NBT int array in and ensure its the proper size.
     *
     * @param stack
     * @param size
     * @return a usable int[] for the bag storage.
     */
    static int[] getStorageArray(final ItemStack stack, final int size) {
        int[] out = null;
        CompoundTag compound = stack.getTag();

        if (compound == null) compound = new CompoundTag();

        if (compound.contains("contents")) {
            out = compound.getIntArray("contents");
        }

        if (out == null) {
            stack.setTag(compound);
            out = new int[size];
            compound.putIntArray("contents", out);
        }

        if (out.length != size && compound != null) {
            final int[] tmp = out;
            out = new int[size];
            System.arraycopy(out, 0, tmp, 0, Math.min(size, tmp.length));
            compound.putIntArray("contents", out);
        }

        return out;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, @Nullable final Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            setStorage(getStorageArray(stack, BAG_STORAGE_SLOTS * ItemBitBag.INTS_PER_BIT_TYPE));
            return capResult.cast();
        }

        return LazyOptional.empty();
    }
}
