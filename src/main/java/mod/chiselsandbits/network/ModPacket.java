package mod.chiselsandbits.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public abstract class ModPacket implements FabricPacket {

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
    public void write(FriendlyByteBuf buf) {
        getPayload(buf);
    }
}
