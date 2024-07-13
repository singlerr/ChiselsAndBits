package mod.chiselsandbits.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.InterModComms;

public class IMCHandlerMaterialEquivilancy implements IMCMessageHandler {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void excuteIMC(final InterModComms.IMCMessage message) {
        try {
            final Supplier<Optional<Function<Map, Void>>> functionSupplier = message.getMessageSupplier();
            final Optional<Function<Map, Void>> map = functionSupplier.get();

            final Map<Object, Object> obj = new HashMap();
            map.get().apply(obj);

            for (final Entry<Object, Object> set : obj.entrySet()) {
                if (set.getKey() instanceof Material && set.getValue() instanceof Material) {
                    ChiselsAndBits.getApi().addEquivilantMaterial((Material) set.getKey(), (Material) set.getValue());
                } else {
                    Log.info("Expected materials for keys and values but got something else - IMC: "
                            + message.getMethod());
                }
            }
        } catch (final Throwable e) {
            Log.logError("IMC materialequivilancy From " + message.getSenderModId(), e);
        }
    }
}
