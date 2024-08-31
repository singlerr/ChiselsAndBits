package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketSortBagGui extends ModPacket {

    public static final PacketType<PacketSortBagGui> PACKET_TYPE =
            PacketType.create(new ResourceLocation(Constants.MOD_ID, "packet_sort_bag_gui"), PacketSortBagGui::new);

    public PacketSortBagGui(FriendlyByteBuf buffer) {
        readPayload(buffer);
    }

    public PacketSortBagGui() {}

    @Override
    public void server(final ServerPlayer player) {
        if (player.containerMenu instanceof BagContainer) {
            ((BagContainer) player.containerMenu).sort();
        }
    }

    @Override
    public void getPayload(FriendlyByteBuf buffer) {}

    @Override
    public void readPayload(FriendlyByteBuf buffer) {}

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }
}
