package mod.chiselsandbits.network;

import java.util.function.Function;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Our wrapper for Forge network layer
 */
public class NetworkChannel {
    private static final String LATEST_PROTO_VER = "1.0";
    private static final String ACCEPTED_PROTO_VERS = LATEST_PROTO_VER;
    /**
     * Forge network channel
     */
    private final SimpleChannel rawChannel;

    /**
     * Creates a new instance of network channel.
     *
     * @param channelName unique channel name
     * @throws IllegalArgumentException if channelName already exists
     */
    public NetworkChannel(final String channelName) {
        rawChannel = new SimpleChannel(new ResourceLocation("chiselsandbits", channelName));
    }

    /**
     * Registers all common messages.
     */
    public void registerCommonMessages() {
        ModPacketTypes.init(this);
    }

    /**
     * Register a message into rawChannel.
     *
     * @param <MSG>      message class type
     * @param id         network id
     * @param msgClazz   message class
     * @param msgCreator supplier with new instance of msgClazz
     */
    public <MSG extends ModPacket> void registerMessage(
            int id, final Class<MSG> msgClazz, final Function<FriendlyByteBuf, MSG> msgCreator) {
        rawChannel.registerC2SPacket(C2SPacketDelegate.class, id, (buf) -> {
            return new C2SPacketDelegate(msgCreator.apply(buf));
        });

        rawChannel.registerS2CPacket(S2CPacketDelegate.class, ++id, buf -> {
            return new S2CPacketDelegate(msgCreator.apply(buf));
        });
    }

    /**
     * Sends to server.
     *
     * @param msg message to send
     */
    public void sendToServer(final ModPacket msg) {
        rawChannel.sendToServer(new C2SPacketDelegate(msg));
    }

    /**
     * Sends to player.
     *
     * @param msg    message to send
     * @param player target player
     */
    public void sendToPlayer(final ModPacket msg, final ServerPlayer player) {
        rawChannel.sendToClient(new S2CPacketDelegate(msg), player);
    }

    /**
     * Sends to origin client.
     *
     * @param msg message to send
     * @param ctx network context
     */
    public void sendToOrigin(final ModPacket msg, final Context ctx) {
        final ServerPlayer player = ctx.serverPlayer();
        if (player != null) // side check
        {
            sendToPlayer(msg, player);
        } else {
            sendToServer(msg);
        }
    }

    /**
     * Sends to everyone.
     *
     * @param msg message to send
     */
    public void sendToEveryone(final ModPacket msg) {
        rawChannel.sendToClientsInCurrentServer(new S2CPacketDelegate(msg));
    }

    /**
     * Sends to everyone who is in range from entity's pos using formula below.
     *
     * <pre>
     * Math.min(Entity.getType().getTrackingRange(), ChunkManager.this.viewDistance - 1) * 16;
     * </pre>
     * <p>
     * as of 24-06-2019
     *
     * @param msg    message to send
     * @param entity target entity to look at
     */
    public void sendToTrackingEntity(final ModPacket msg, final Entity entity) {
        rawChannel.sendToClientsTracking(new S2CPacketDelegate(msg), entity);
    }

    /**
     * Sends to everyone (including given entity) who is in range from entity's pos using formula below.
     *
     * <pre>
     * Math.min(Entity.getType().getTrackingRange(), ChunkManager.this.viewDistance - 1) * 16;
     * </pre>
     * <p>
     * as of 24-06-2019
     *
     * @param msg    message to send
     * @param entity target entity to look at
     */
    public void sendToTrackingEntityAndSelf(final ModPacket msg, final Entity entity) {
        rawChannel.sendToClientsTrackingAndSelf(new S2CPacketDelegate(msg), entity);
    }

    public record Context(@Nullable ServerPlayer serverPlayer, PacketSender packetSender, SimpleChannel channel) {}
}
