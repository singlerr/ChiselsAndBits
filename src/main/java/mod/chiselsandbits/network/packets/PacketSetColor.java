package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class PacketSetColor extends ModPacket {

    private DyeColor newColor = DyeColor.WHITE;
    private ChiselToolType type = ChiselToolType.TAPEMEASURE;
    private boolean chatNotification = false;

    public PacketSetColor(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    public PacketSetColor(final DyeColor newColor, final ChiselToolType type, final boolean chatNotification) {
        this.newColor = newColor;
        this.type = type;
        this.chatNotification = chatNotification;
    }

    @Override
    public void server(final ServerPlayer player) {
        final ItemStack ei = player.getMainHandItem();
        if (ei != null && ei.getItem() instanceof IChiselModeItem) {
            final DyeColor originalMode = getColor(ei);
            setColor(ei, newColor);

            if (originalMode != newColor && chatNotification) {
                player.sendSystemMessage(Component.translatable("chiselsandbits.color." + newColor.getName()));
            }
        }
    }

    private void setColor(final ItemStack ei, final DyeColor newColor2) {
        if (ei != null) {
            ei.addTagElement("color", StringTag.valueOf(newColor2.name()));
        }
    }

    private DyeColor getColor(final ItemStack ei) {
        try {
            if (ei != null && ei.hasTag()) {
                return DyeColor.valueOf(ModUtil.getTagCompound(ei).getString("color"));
            }
        } catch (final IllegalArgumentException e) {
            // nope!
        }

        return DyeColor.WHITE;
    }

    @Override
    public void getPayload(final FriendlyByteBuf buffer) {
        buffer.writeBoolean(chatNotification);
        buffer.writeEnum(type);
        buffer.writeEnum(newColor);
    }

    @Override
    public void readPayload(final FriendlyByteBuf buffer) {
        chatNotification = buffer.readBoolean();
        type = buffer.readEnum(ChiselToolType.class);
        newColor = buffer.readEnum(DyeColor.class);
    }
}
