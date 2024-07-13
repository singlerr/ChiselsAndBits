package mod.chiselsandbits.helpers;

import java.util.*;
import mod.chiselsandbits.modes.ChiselMode;
import mod.chiselsandbits.modes.IToolMode;
import mod.chiselsandbits.modes.PositivePatternMode;
import mod.chiselsandbits.modes.TapeMeasureModes;
import net.minecraft.world.item.ItemStack;

public enum ChiselToolType {
    CHISEL(true, true),
    BIT(true, false),

    CHISELED_BLOCK(true, false),

    POSITIVEPATTERN(true, true),
    TAPEMEASURE(true, true),
    NEGATIVEPATTERN(true, false),
    MIRRORPATTERN(false, false);

    private final boolean hasMenu;
    private final boolean hasItemSettings;

    private ChiselToolType(final boolean menu, final boolean itemSettings) {
        hasMenu = menu;
        hasItemSettings = itemSettings;
    }

    public IToolMode getMode(final ItemStack ei) {
        if (this == CHISEL) {
            return ChiselMode.getMode(ei);
        }

        if (this == POSITIVEPATTERN) {
            return PositivePatternMode.getMode(ei);
        }

        if (this == ChiselToolType.TAPEMEASURE) {
            return TapeMeasureModes.getMode(ei);
        }

        throw new NullPointerException();
    }

    public boolean hasMenu() {
        return hasMenu;
    }

    public List<IToolMode> getAvailableModes() {
        if (isBitOrChisel()) {
            final List<IToolMode> modes = new ArrayList<IToolMode>();
            final EnumSet<ChiselMode> used = EnumSet.noneOf(ChiselMode.class);
            final ChiselMode[] orderedModes = {
                ChiselMode.SINGLE,
                ChiselMode.LINE,
                ChiselMode.PLANE,
                ChiselMode.CONNECTED_PLANE,
                ChiselMode.CONNECTED_MATERIAL,
                ChiselMode.DRAWN_REGION,
                ChiselMode.SAME_MATERIAL
            };

            for (final ChiselMode mode : orderedModes) {
                if (!mode.isDisabled) {
                    modes.add(mode);
                    used.add(mode);
                }
            }

            for (final ChiselMode mode : ChiselMode.values()) {
                if (!mode.isDisabled && !used.contains(mode)) {
                    modes.add(mode);
                }
            }

            return modes;
        } else if (this == POSITIVEPATTERN) {
            return asArray(PositivePatternMode.values());
        } else if (this == TAPEMEASURE) {
            return asArray(TapeMeasureModes.values());
        } else {
            return Collections.emptyList();
        }
    }

    private List<IToolMode> asArray(final Object[] values) {
        return Arrays.asList((IToolMode[]) values);
    }

    public boolean isBitOrChisel() {
        return this == BIT || this == ChiselToolType.CHISEL;
    }

    public boolean hasPerToolSettings() {
        return hasItemSettings;
    }

    public boolean requiresPerToolSettings() {
        return this == ChiselToolType.POSITIVEPATTERN || this == ChiselToolType.TAPEMEASURE;
    }
}
