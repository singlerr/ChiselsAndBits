package mod.chiselsandbits.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@SuppressWarnings("rawtypes")
public abstract class ModPacket implements C2SPacket, S2CPacket {

    ServerPlayer serverEntity = null;

    public ModPacket() {}

    public ModPacket(FriendlyByteBuf buf) {
        readPayload(buf);
    }

    public void server(final ServerPlayer playerEntity) {
        throw new RuntimeException(getClass().getName() + " is not a server packet.");
    }

    public void client() {
        throw new RuntimeException(getClass().getName() + " is not a client packet.");
    }

    public abstract void getPayload(FriendlyByteBuf buffer);

    public abstract void readPayload(FriendlyByteBuf buffer);

    public void processPacket(final NetworkChannel.Context context, final Boolean onServer) {
        if (!onServer) {
            client();
        } else {
            serverEntity = context.serverPlayer();
            server(serverEntity);
        }
    }

    @Override
    public void handle(
            MinecraftServer server,
            ServerPlayer player,
            ServerGamePacketListenerImpl listener,
            PacketSender responseSender,
            SimpleChannel channel) {
        processPacket(new NetworkChannel.Context(serverEntity, responseSender, channel), true);
    }

    @Override
    public void handle(
            Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        processPacket(new NetworkChannel.Context(null, responseSender, channel), false);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        getPayload(buf);
    }
}
