package mod.chiselsandbits.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@SuppressWarnings("rawtypes")
public class C2SPacketDelegate implements C2SPacket {

    private final ModPacket deco;

    public C2SPacketDelegate(ModPacket deco) {
        this.deco = deco;
    }

    @Override
    public void handle(
            MinecraftServer server,
            ServerPlayer player,
            ServerGamePacketListenerImpl listener,
            PacketSender responseSender,
            SimpleChannel channel) {
        deco.processPacket(new NetworkChannel.Context(player, responseSender, channel), true);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        deco.getPayload(buf);
    }
}
