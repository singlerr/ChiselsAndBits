package mod.chiselsandbits.bitstorage;

import java.util.List;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.helpers.LocalStrings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemBlockBitStorage extends BlockItem {

  public ItemBlockBitStorage(final Block block, Item.Properties builder) {
    super(block, builder);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void appendHoverText(
      final ItemStack stack,
      @Nullable final Level worldIn,
      final List<Component> tooltip,
      final TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
      return;
    }

    FluidStack fluid = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
        .map(h -> h.getFluidInTank(0))
        .orElse(FluidStack.EMPTY);

    if (fluid.isEmpty()) {
      ChiselsAndBits.getConfig().getCommon().helpText(LocalStrings.HelpBitTankEmpty, tooltip);
    } else {
      ChiselsAndBits.getConfig()
          .getCommon()
          .helpText(
              LocalStrings.HelpBitTankFilled,
              tooltip,
              DeprecationHelper.translateToLocal(fluid.getTranslationKey()),
              String.valueOf((int) Math.floor(fluid.getAmount() * 4.096)));
    }
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack,
                                              @Nullable final CompoundTag nbt) {
    return new FluidHandlerItemStack(stack, FluidAttributes.BUCKET_VOLUME);
  }

  @Override
  protected boolean updateCustomBlockEntityTag(
      final BlockPos pos,
      final Level worldIn,
      @Nullable final Player player,
      final ItemStack stack,
      final BlockState state) {
    super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
    if (worldIn.isClientSide) {
      return false;
    }

    final BlockEntity tileEntity = worldIn.getBlockEntity(pos);
    if (!(tileEntity instanceof TileEntityBitStorage tileEntityBitStorage)) {
      return false;
    }

    tileEntityBitStorage
        .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        .ifPresent(t -> t.fill(
            stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                .map(s -> s.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE))
                .orElse(FluidStack.EMPTY),
            IFluidHandler.FluidAction.EXECUTE));

    return true;
  }
}
