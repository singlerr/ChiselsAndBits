package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PacketClearBagGui extends ModPacket {

    public static final PacketType<PacketClearBagGui> PACKET_TYPE =
            PacketType.create(new ResourceLocation(Constants.MOD_ID, "packet_clear_bag_gui"), PacketClearBagGui::new);

    private ItemStack stack = null;

    public PacketClearBagGui(final FriendlyByteBuf buffer) {
        readPayload(buffer);
    }

    public PacketClearBagGui(final ItemStack inHandItem) {
        stack = inHandItem;
    }

    @Override
    public void server(final ServerPlayer player) {
        if (player.containerMenu instanceof BagContainer) {
            ((BagContainer) player.containerMenu).clear(stack);
        }
    }

    @Override
    public void getPayload(final FriendlyByteBuf buffer) {
        buffer.writeItem(stack);
        // no data...
    }

    @Override
    public void readPayload(final FriendlyByteBuf buffer) {
        stack = buffer.readItem();
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }
}
