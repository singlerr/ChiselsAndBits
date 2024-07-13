package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PacketSortBagGui extends ModPacket {
    public PacketSortBagGui(FriendlyByteBuf buffer) {
        super(buffer);
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
}
