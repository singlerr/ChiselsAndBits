package mod.chiselsandbits.network;

import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

/**
 * Our wrapper for Forge network layer
 */
public class NetworkChannel {
    private static final String LATEST_PROTO_VER = "1.0";
    private static final String ACCEPTED_PROTO_VERS = LATEST_PROTO_VER;

    private final ResourceLocation networkId;
    /**
     * Creates a new instance of network channel.
     *
     * @param channelName unique channel name
     * @throws IllegalArgumentException if channelName already exists
     */
    public NetworkChannel(final String channelName) {
        networkId = new ResourceLocation(Constants.MOD_ID, channelName);
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
     * @param msgClazz   message class
     */
    public <MSG extends ModPacket> void registerMessage(final Class<MSG> msgClazz, PacketType<MSG> packetType) {
        ClientPlayNetworking.registerGlobalReceiver(
                packetType, (packet, player, responseSender) -> packet.processPacket(null, false));
        ServerPlayNetworking.registerGlobalReceiver(
                packetType,
                (packet, player, responseSender) -> packet.processPacket(new Context(player, responseSender), true));
    }
    /**
     * Sends to server.
     *
     * @param msg message to send
     */
    public void sendToServer(final ModPacket msg) {
        ClientPlayNetworking.send(msg);
    }

    /**
     * Sends to player.
     *
     * @param msg    message to send
     * @param player target player
     */
    public void sendToPlayer(final ModPacket msg, final ServerPlayer player) {
        ServerPlayNetworking.send(player, msg);
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
        throw new NotImplementedException();
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
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    public record Context(@Nullable ServerPlayer serverPlayer, PacketSender packetSender) {}
}
