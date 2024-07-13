package mod.chiselsandbits.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class TickHandler {

    private static long clientTicks = 0;

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(TickHandler::onTickClientTick);
    }

    private static void onTickClientTick(Minecraft inst) {
        clientTicks++;
    }

    public static long getClientTicks() {
        return clientTicks;
    }
}
