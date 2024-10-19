package mod.chiselsandbits.modes;

import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

public enum ChiselMode implements IToolMode {
    SINGLE(LocalStrings.ChiselModeSingle),
    SNAP2(LocalStrings.ChiselModeSnap2),
    SNAP4(LocalStrings.ChiselModeSnap4),
    SNAP8(LocalStrings.ChiselModeSnap8),
    LINE(LocalStrings.ChiselModeLine),
    PLANE(LocalStrings.ChiselModePlane),
    CONNECTED_PLANE(LocalStrings.ChiselModeConnectedPlane),
    CUBE_SMALL(LocalStrings.ChiselModeCubeSmall),
    CUBE_MEDIUM(LocalStrings.ChiselModeCubeMedium),
    CUBE_LARGE(LocalStrings.ChiselModeCubeLarge),
    SAME_MATERIAL(LocalStrings.ChiselModeSameMaterial),
    DRAWN_REGION(LocalStrings.ChiselModeDrawnRegion),
    CONNECTED_MATERIAL(LocalStrings.ChiselModeConnectedMaterial);

    public final LocalStrings string;

    public boolean isDisabled = false;

    public Object binding;

    ChiselMode(final LocalStrings str) {
        string = str;
    }

    public static ChiselMode getMode(final ItemStack stack) {
        if (stack != null) {
            try {
                final CompoundTag nbt = stack.getTag();
                if (nbt != null && nbt.contains("mode")) {
                    return valueOf(nbt.getString("mode"));
                }
            } catch (final IllegalArgumentException iae) {
                // nope!
            } catch (final Exception e) {
                Log.logError("Unable to determine mode.", e);
            }
        }

        return SINGLE;
    }

    public static ChiselMode castMode(final IToolMode chiselMode) {
        if (chiselMode instanceof ChiselMode) {
            return (ChiselMode) chiselMode;
        }

        return ChiselMode.SINGLE;
    }

    @Override
    public void setMode(final ItemStack stack) {
        if (stack != null) {
            stack.addTagElement("mode", StringTag.valueOf(name()));
        }
    }

    @Override
    public LocalStrings getName() {
        return string;
    }

    @Override
    public boolean isDisabled() {
        return isDisabled;
    }
}
