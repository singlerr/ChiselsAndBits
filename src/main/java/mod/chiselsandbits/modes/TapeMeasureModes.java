package mod.chiselsandbits.modes;

import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

public enum TapeMeasureModes implements IToolMode {
  BIT(LocalStrings.TapeMeasureBit),
  BLOCK(LocalStrings.TapeMeasureBlock),
  DISTANCE(LocalStrings.TapeMeasureDistance);

  public final LocalStrings string;
  public boolean isDisabled = false;

  public Object binding;

  private TapeMeasureModes(final LocalStrings str) {
    string = str;
  }

  public static TapeMeasureModes getMode(final ItemStack stack) {
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

    return TapeMeasureModes.BIT;
  }

  public static TapeMeasureModes castMode(final IToolMode chiselMode) {
    if (chiselMode instanceof TapeMeasureModes) {
      return (TapeMeasureModes) chiselMode;
    }

    return TapeMeasureModes.BIT;
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
