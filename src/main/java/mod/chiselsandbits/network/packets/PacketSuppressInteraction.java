package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.events.EventPlayerInteract;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketSuppressInteraction extends ModPacket {

  public static final PacketType<PacketSuppressInteraction> PACKET_TYPE = PacketType.create(
      new ResourceLocation(Constants.MOD_ID, "packet_suppress_interaction"),
      PacketSuppressInteraction::new);

  private boolean newSetting = false;

  public PacketSuppressInteraction(final FriendlyByteBuf buffer) {
    readPayload(buffer);
  }

  public PacketSuppressInteraction(final boolean newSetting) {
    this.newSetting = newSetting;
  }

  @Override
  public void server(final ServerPlayer player) {
    EventPlayerInteract.setPlayerSuppressionState(player, newSetting);
  }

  @Override
  public void getPayload(final FriendlyByteBuf buffer) {
    buffer.writeBoolean(newSetting);
  }

  @Override
  public void readPayload(final FriendlyByteBuf buffer) {
    newSetting = buffer.readBoolean();
  }

  @Override
  public PacketType<?> getType() {
    return PACKET_TYPE;
  }
}
