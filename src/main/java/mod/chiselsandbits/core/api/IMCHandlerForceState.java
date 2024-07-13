package mod.chiselsandbits.core.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.Log;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.InterModComms;

public class IMCHandlerForceState implements IMCMessageHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void excuteIMC(final InterModComms.IMCMessage message) {
        try {

            final Supplier<Optional<Function<List<Block>, Boolean>>> methodSupplier = message.getMessageSupplier();
            final Optional<Function<List<Block>, Boolean>> method = methodSupplier.get();

            if (method.isPresent()) {
                final Function<List<Block>, Boolean> targetMethod = method.get();
                final ArrayList<Block> o = new ArrayList<Block>();
                final Boolean result = targetMethod.apply(o);

                if (result == null) {
                    Log.info(message.getSenderModId() + ", Your IMC returns null, must be true or false for "
                            + message.getMethod());
                } else {
                    o.forEach(x -> BlockBitInfo.forceStateCompatibility(x, result));
                }
            } else {
                Log.info(
                        message.getSenderModId() + ", Your IMC must be a functional message, 'Boolean apply( List )'.");
            }
        } catch (final Throwable e) {
            Log.logError("IMC forcestatecompatibility From " + message.getMethod(), e);
        }
    }
}
