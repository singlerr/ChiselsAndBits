package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class PacketOpenBagGui extends ModPacket {

  public static final PacketType<PacketOpenBagGui> PACKET_TYPE =
      PacketType.create(new ResourceLocation(Constants.MOD_ID, "packet_open_bag_gui"),
          PacketOpenBagGui::new);

  public PacketOpenBagGui(FriendlyByteBuf buffer) {
    readPayload(buffer);
  }

  public PacketOpenBagGui() {
  }

  @Override
  public void server(final ServerPlayer player) {
    player.openMenu(new SimpleMenuProvider(
        (id, playerInventory, playerEntity) -> new BagContainer(id, playerInventory),
        Component.literal("Bitbag")));
  }

  @Override
  public void getPayload(final FriendlyByteBuf buffer) {
    // no data...
  }

  @Override
  public void readPayload(final FriendlyByteBuf buffer) {
    // no data..
  }

  @Override
  public PacketType<?> getType() {
    return PACKET_TYPE;
  }
}
