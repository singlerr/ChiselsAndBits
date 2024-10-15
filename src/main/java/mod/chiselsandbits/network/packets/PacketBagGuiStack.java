package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.network.ModPacket;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class PacketBagGuiStack extends ModPacket {

  public static final PacketType<PacketBagGuiStack> PACKET_TYPE =
      PacketType.create(new ResourceLocation(Constants.MOD_ID, "packet_bag_gui_stack"),
          PacketBagGuiStack::new);

  private int index = -1;
  private ItemStack is;

  public PacketBagGuiStack(FriendlyByteBuf buffer) {
    readPayload(buffer);
  }

  public PacketBagGuiStack(final int index, final ItemStack is) {
    this.index = index;
    this.is = is;
  }

  @Override
  public void client() {
    final AbstractContainerMenu cc = ClientSide.instance.getPlayer().containerMenu;
    if (cc instanceof BagContainer) {
      ((BagContainer) cc).customSlots.get(index).set(is);
    }
  }

  @Override
  public void getPayload(final FriendlyByteBuf buffer) {
    buffer.writeInt(index);

    if (is == null) {
      buffer.writeInt(0);
    } else {
      buffer.writeInt(ModUtil.getStackSize(is));
      buffer.writeInt(ItemChiseledBit.getStackState(is));
    }
  }

  @Override
  public void readPayload(final FriendlyByteBuf buffer) {
    index = buffer.readInt();

    final int size = buffer.readInt();

    if (size <= 0) {
      is = null;
    } else {
      is = ItemChiseledBit.createStack(buffer.readInt(), size, false);
    }
  }

  @Override
  public PacketType<?> getType() {
    return PACKET_TYPE;
  }
}
