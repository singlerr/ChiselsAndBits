package mod.chiselsandbits.network;

import java.util.HashMap;
import java.util.function.Consumer;
import mod.chiselsandbits.network.packets.PacketAccurateSneakPlace;
import mod.chiselsandbits.network.packets.PacketBagGui;
import mod.chiselsandbits.network.packets.PacketBagGuiStack;
import mod.chiselsandbits.network.packets.PacketChisel;
import mod.chiselsandbits.network.packets.PacketClearBagGui;
import mod.chiselsandbits.network.packets.PacketOpenBagGui;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.network.packets.PacketSetChiselMode;
import mod.chiselsandbits.network.packets.PacketSetColor;
import mod.chiselsandbits.network.packets.PacketSortBagGui;
import mod.chiselsandbits.network.packets.PacketSuppressInteraction;
import mod.chiselsandbits.network.packets.PacketUndo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ModPacketTypes {
    CHISEL((channel) -> {
        channel.registerMessage(PacketChisel.class, PacketChisel.PACKET_TYPE);
    }),
    OPEN_BAG_GUI((channel) -> {
        channel.registerMessage(PacketOpenBagGui.class, PacketOpenBagGui.PACKET_TYPE);
    }),
    SET_CHISEL_MODE((channel) -> {
        channel.registerMessage(PacketSetChiselMode.class, PacketSetChiselMode.PACKET_TYPE);
    }),
    ROTATE_VOXEL_BLOB(((channel) -> {
        channel.registerMessage(PacketRotateVoxelBlob.class, PacketRotateVoxelBlob.PACKET_TYPE);
    })),
    BAG_GUI(((channel) -> {
        channel.registerMessage(PacketBagGui.class, PacketBagGui.PACKET_TYPE);
    })),
    BAG_GUI_STACK(((channel) -> {
        channel.registerMessage(PacketBagGuiStack.class, PacketBagGuiStack.PACKET_TYPE);
    })),
    UNDO(((channel) -> {
        channel.registerMessage(PacketUndo.class, PacketUndo.PACKET_TYPE);
    })),
    CLEAR_BAG(((channel) -> {
        channel.registerMessage(PacketClearBagGui.class, PacketClearBagGui.PACKET_TYPE);
    })),
    SUPRESS_INTERACTION(((channel) -> {
        channel.registerMessage(PacketSuppressInteraction.class, PacketSuppressInteraction.PACKET_TYPE);
    })),
    SET_COLOR(((channel) -> {
        channel.registerMessage(PacketSetColor.class, PacketSetColor.PACKET_TYPE);
    })),
    ACCURATE_PLACEMENT(((channel) -> {
        channel.registerMessage(PacketAccurateSneakPlace.class, PacketAccurateSneakPlace.PACKET_TYPE);
    })),
    SORT_BAG_GUI(((channel) -> {
        channel.registerMessage(PacketSortBagGui.class, PacketSortBagGui.PACKET_TYPE);
    }));

    private static final Logger LOGGER = LogManager.getLogger(ModPacketTypes.class);
    private static final HashMap<Class<? extends ModPacket>, Integer> fromClassToId =
            new HashMap<Class<? extends ModPacket>, Integer>();
    private static final HashMap<Integer, Class<? extends ModPacket>> fromIdToClass =
            new HashMap<Integer, Class<? extends ModPacket>>();
    private final Consumer<NetworkChannel> registrationHandler;

    ModPacketTypes(final Consumer<NetworkChannel> registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    public static void init(NetworkChannel channel) {
        for (final ModPacketTypes p : ModPacketTypes.values()) {
            p.registrationHandler.accept(channel);
        }
    }

    public static int getID(final Class<? extends ModPacket> clz) {
        return fromClassToId.get(clz);
    }

    public static ModPacket constructByID(final int id) throws InstantiationException, IllegalAccessException {
        return fromIdToClass.get(id).newInstance();
    }
}
