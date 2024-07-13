package mod.chiselsandbits.core.api;

import com.mojang.datafixers.util.Pair;
import java.util.function.Supplier;
import mod.chiselsandbits.client.ModConflictContext;
import mod.chiselsandbits.core.Log;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.InterModComms;

public class IMCHandlerKeyBinding implements IMCMessageHandler {

    @Override
    public void excuteIMC(final InterModComms.IMCMessage message) {
        try {
            final Supplier<Pair<String, Item>> itemSupplier = message.getMessageSupplier();
            final Pair<String, Item> item = itemSupplier.get();

            if (item == null || item.getSecond() == Items.AIR) {
                throw new RuntimeException(
                        "Unable to locate item " + item.getSecond().getRegistryName());
            }

            for (ModConflictContext conflictContext : ModConflictContext.values()) {
                if (conflictContext.getName().equals(item.getFirst())) {
                    conflictContext.setItemActive(item.getSecond());
                }
            }
        } catch (final Throwable e) {
            Log.logError("IMC registeritemwithkeybinding From " + message.getSenderModId(), e);
        }
    }
}
