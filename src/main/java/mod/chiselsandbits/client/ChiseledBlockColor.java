package mod.chiselsandbits.client;

import mod.chiselsandbits.helpers.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChiseledBlockColor implements BlockColor {

    public static final int TINT_MASK = 0xff;
    public static final int TINT_BITS = 8;

    @Override
    public int getColor(
            @NotNull final BlockState state,
            @Nullable final BlockAndTintGetter displayReader,
            @Nullable final BlockPos pos,
            final int color) {
        final BlockState containedState = ModUtil.getStateById(color >> TINT_BITS);
        int tintValue = color & TINT_MASK;
        return Minecraft.getInstance().getBlockColors().getColor(containedState, displayReader, pos, tintValue);
    }
}
