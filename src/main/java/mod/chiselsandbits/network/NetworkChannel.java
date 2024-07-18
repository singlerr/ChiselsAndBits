package mod.chiselsandbits.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        rawChannel.initClientListener();
        rawChannel.initServerListener();
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
        rawChannel.registerC2SPacket(msgClazz, id++, msgCreator);
        rawChannel.registerS2CPacket(msgClazz, id, msgCreator);
    }

    private <MSG extends ModPacket> MSG createPacket(Class<?> clz, FriendlyByteBuf buf) {
        try {

            Constructor<?> c = clz.getConstructor(FriendlyByteBuf.class);
            return (MSG) c.newInstance(buf);
        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sends to server.
     *
     * @param msg message to send
     */
    public void sendToServer(final ModPacket msg) {
        rawChannel.sendToServer(msg);
    }

    /**
     * Sends to player.
     *
     * @param msg    message to send
     * @param player target player
     */
    public void sendToPlayer(final ModPacket msg, final ServerPlayer player) {
        rawChannel.sendToClient(msg, player);
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
        rawChannel.sendToClientsInCurrentServer(msg);
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
        rawChannel.sendToClientsTracking(msg, entity);
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
        rawChannel.sendToClientsTrackingAndSelf(msg, entity);
    }

    public record Context(@Nullable ServerPlayer serverPlayer, PacketSender packetSender, SimpleChannel channel) {}
}
