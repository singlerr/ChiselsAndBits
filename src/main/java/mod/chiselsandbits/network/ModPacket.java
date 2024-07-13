package mod.chiselsandbits.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("rawtypes")
public abstract class ModPacket {

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
}
