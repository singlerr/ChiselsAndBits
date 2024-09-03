package mod.chiselsandbits.helpers;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import mod.chiselsandbits.utils.EnvExecutor;
import mod.chiselsandbits.utils.LanguageHandler;
import mod.chiselsandbits.utils.SingleBlockBlockReader;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class DeprecationHelper {

    public static int getLightValue(final BlockState state) {
        if (state.getBlock() instanceof IBlockWithWorldlyProperties prop) {
            return prop.getLightEmission(state, new SingleBlockBlockReader(state, state.getBlock()), BlockPos.ZERO);
        }
        return state.getLightEmission();
    }

    public static BlockState getStateFromItem(final ItemStack bitItemStack) {
        if (bitItemStack != null && bitItemStack.getItem() instanceof BlockItem) {
            final BlockItem blkItem = (BlockItem) bitItemStack.getItem();
            return blkItem.getBlock().defaultBlockState();
        }

        return null;
    }

    public static String translateToLocal(final String string) {
        return EnvExecutor.unsafeRunForDist(
                () -> () -> {
                    final String translated = Language.getInstance().getOrDefault(string);
                    if (translated.equals(string)) return LanguageHandler.translateKey(string);

                    return translated;
                },
                () -> () -> LanguageHandler.translateKey(string));
    }

    public static String translateToLocal(final String string, final Object... args) {
        return String.format(translateToLocal(string), args);
    }

    public static SoundType getSoundType(BlockState block) {
        return block.getBlock().soundType;
    }

    public static SoundType getSoundType(Block block) {
        return block.soundType;
    }
}
