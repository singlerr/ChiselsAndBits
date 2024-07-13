package mod.chiselsandbits.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class S2CPacketDelegate implements S2CPacket {

    private final ModPacket deco;

    public S2CPacketDelegate(ModPacket deco) {
        this.deco = deco;
    }

    @Override
    public void handle(
            Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        deco.processPacket(new NetworkChannel.Context(null, responseSender, channel), false);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        deco.getPayload(buf);
    }
}
