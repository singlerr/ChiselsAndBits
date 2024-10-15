package mod.chiselsandbits.events;

import mod.chiselsandbits.items.ItemMagnifyingGlass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class ClientTickHandler {

  public static void register() {
    ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onTickPlayerTick);
  }

  @Environment(EnvType.CLIENT)
  private static void onTickPlayerTick(Minecraft event) {
    if (Minecraft.getInstance().player != null) {
      if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof ItemMagnifyingGlass
          ||
          Minecraft.getInstance().player.getOffhandItem()
              .getItem() instanceof ItemMagnifyingGlass) {
        if (Minecraft.getInstance().gui != null) {
          Minecraft.getInstance().gui.toolHighlightTimer = 40;
        }
      }
    }
  }
}
