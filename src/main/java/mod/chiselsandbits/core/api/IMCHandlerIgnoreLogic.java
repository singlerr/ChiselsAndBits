package mod.chiselsandbits.core.api;

import java.util.function.Supplier;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.Log;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.InterModComms;

public class IMCHandlerIgnoreLogic implements IMCMessageHandler {

    @Override
    public void excuteIMC(final InterModComms.IMCMessage message) {
        try {

            final Supplier<Block> blockSupplier = message.getMessageSupplier();
            final Block blk = blockSupplier.get();

            if (blk != null && blk != Blocks.AIR) {
                BlockBitInfo.ignoreBlockLogic(blk);
            } else {
                throw new RuntimeException("Unable to locate block " + blk.getRegistryName());
            }
        } catch (final Throwable e) {
            Log.logError("IMC ignoreblocklogic From " + message.getSenderModId(), e);
        }
    }
}
