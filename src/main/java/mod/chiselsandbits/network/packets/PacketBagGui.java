package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PacketBagGui extends ModPacket {

    public static final PacketType<PacketBagGui> PACKET_TYPE =
            PacketType.create(new ResourceLocation(Constants.MOD_ID, "packet_bag_gui"), PacketBagGui::new);

    private int slotNumber = -1;
    private int mouseButton = -1;
    private boolean duplicateButton = false;
    private boolean holdingShift = false;

    public PacketBagGui(FriendlyByteBuf buffer) {
        readPayload(buffer);
    }

    public PacketBagGui(
            final int slotNumber, final int mouseButton, final boolean duplicateButton, final boolean holdingShift) {
        this.slotNumber = slotNumber;
        this.mouseButton = mouseButton;
        this.duplicateButton = duplicateButton;
        this.holdingShift = holdingShift;
    }

    @Override
    public void server(final ServerPlayer player) {
        doAction(player);
    }

    public void doAction(final Player player) {
        final AbstractContainerMenu c = player.containerMenu;
        if (c instanceof BagContainer bc) {
            bc.handleCustomSlotAction(slotNumber, mouseButton, duplicateButton, holdingShift);
        }
    }

    @Override
    public void getPayload(final FriendlyByteBuf buffer) {
        buffer.writeInt(slotNumber);
        buffer.writeInt(mouseButton);
        buffer.writeBoolean(duplicateButton);
        buffer.writeBoolean(holdingShift);
    }

    @Override
    public void readPayload(final FriendlyByteBuf buffer) {
        slotNumber = buffer.readInt();
        mouseButton = buffer.readInt();
        duplicateButton = buffer.readBoolean();
        holdingShift = buffer.readBoolean();
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }
}
