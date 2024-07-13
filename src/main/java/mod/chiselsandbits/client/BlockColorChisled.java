package mod.chiselsandbits.client;

import mod.chiselsandbits.helpers.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockColorChisled implements BlockColor {

    public static final int TINT_MASK = 0xff;
    public static final int TINT_BITS = 8;

    @Override
    public int getColor(
            final BlockState p_getColor_1_,
            @Nullable final BlockAndTintGetter p_getColor_2_,
            @Nullable final BlockPos p_getColor_3_,
            final int p_getColor_4_) {

        final BlockState tstate = ModUtil.getStateById(p_getColor_4_ >> TINT_BITS);
        int tintValue = p_getColor_4_ & TINT_MASK;
        return Minecraft.getInstance().getBlockColors().getColor(tstate, p_getColor_2_, p_getColor_3_, tintValue);
    }
}
