package mod.chiselsandbits.utils;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;

public class PaletteUtils {

  private PaletteUtils() {
    throw new IllegalStateException(
        "Can not instantiate an instance of: PaletteUtils. This is a utility class");
  }

  public static List<BlockState> getOrderedListInPalette(final Palette<BlockState> stateIPalette) {
    if (stateIPalette instanceof LinearPalette) {
      return Arrays.asList(((LinearPalette<BlockState>) stateIPalette).values);
    }

    if (stateIPalette instanceof HashMapPalette) {
      final CrudeIncrementalIntIdentityHashBiMap<BlockState> map =
          ((HashMapPalette<BlockState>) stateIPalette).values;

      final List<BlockState> dataList = Lists.newArrayList(map);
      dataList.sort(Comparator.comparing(map::getId));

      return dataList;
    }

    if (stateIPalette instanceof GlobalPalette) {
      final List<BlockState> dataList = Lists.newArrayList(Block.BLOCK_STATE_REGISTRY);
      dataList.sort(Comparator.comparing(Block.BLOCK_STATE_REGISTRY::getId));

      return dataList;
    }

    throw new IllegalArgumentException("The given palette type is unknown.");
  }

  public static void read(final Palette<BlockState> stateIPalette, final FriendlyByteBuf buffer) {
    if (stateIPalette instanceof LinearPalette) {
      final LinearPalette<BlockState> palette = (LinearPalette<BlockState>) stateIPalette;
      palette.size = buffer.readVarInt();

      final Object[] statesArray = palette.values;

      for (int i = 0; i < palette.size; ++i) {
        final Object registryEntry = palette.registry.byId(buffer.readVarInt());
        statesArray[i] = registryEntry;
      }
    }

    if (stateIPalette instanceof HashMapPalette) {
      final HashMapPalette<BlockState> palette = (HashMapPalette<BlockState>) stateIPalette;
      palette.values.clear();
      int i = buffer.readVarInt();

      for (int j = 0; j < i; ++j) {
        palette.values.add(palette.registry.byId(buffer.readVarInt()));
      }
    }
  }
}
