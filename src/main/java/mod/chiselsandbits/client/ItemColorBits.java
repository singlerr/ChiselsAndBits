package mod.chiselsandbits.client;

import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.render.helpers.ModelUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemColorBits implements ItemColor {

    @Override
    public int getColor(final ItemStack stack, final int tint) {
        if (ClientSide.instance.holdingShift()) {
            final BlockState state = ModUtil.getStateById(tint >> BlockColorChisled.TINT_BITS);
            final Block blk = state.getBlock();
            final Item i = Item.byBlock(blk);
            int tintValue = tint & BlockColorChisled.TINT_MASK;

            if (i != null) {
                return ModelUtil.getItemStackColor(new ItemStack(i, 1), tintValue);
            }

            return 0xffffff;
        }

        final BlockState state = ModUtil.getStateById(ItemChiseledBit.getStackState(stack));
        return state == null ? 0xffffffff : BlockBitInfo.getColorFor(state, tint);
    }
}
