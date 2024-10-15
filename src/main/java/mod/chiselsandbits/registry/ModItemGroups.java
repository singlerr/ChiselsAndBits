package mod.chiselsandbits.registry;

import com.google.common.base.Suppliers;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public final class ModItemGroups {

  public static final Supplier<CreativeModeTab> MAIN_TAB =
      Suppliers.memoize(() -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
          .icon(() -> new ItemStack(ModItems.ITEM_BIT_BAG_DYED.get()))
          .title(Component.literal("Chisels and bits"))
          .displayItems((params, output) -> {
            output.accept(new ItemStack(ModItems.ITEM_CHISEL_STONE.get()));
            output.accept(new ItemStack(ModItems.ITEM_CHISEL_IRON.get()));
            output.accept(new ItemStack(ModItems.ITEM_CHISEL_GOLD.get()));
            output.accept(new ItemStack(ModItems.ITEM_CHISEL_DIAMOND.get()));
            output.accept(new ItemStack(ModItems.ITEM_CHISEL_NETHERITE.get()));
            output.accept(new ItemStack(ModItems.ITEM_BIT_BAG_DEFAULT.get()));
            output.accept(new ItemStack(ModItems.ITEM_BIT_BAG_DYED.get()));
            output.accept(new ItemStack(ModItems.ITEM_POSITIVE_PRINT.get()));
            output.accept(new ItemStack(ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get()));
            output.accept(new ItemStack(ModItems.ITEM_MAGNIFYING_GLASS.get()));
            output.accept(new ItemStack(ModItems.ITEM_TAPE_MEASURE.get()));
            output.accept(new ItemStack(ModItems.ITEM_NEGATIVE_PRINT.get()));
            output.accept(new ItemStack(ModItems.ITEM_NEGATIVE_PRINT_WRITTEN.get()));
            output.accept(new ItemStack(ModItems.ITEM_MIRROR_PRINT.get()));
            output.accept(new ItemStack(ModItems.ITEM_MIRROR_PRINT_WRITTEN.get()));
          })
          .build());
  public static final Supplier<CreativeModeTab> BLOCK_BITS =
      Suppliers.memoize(() -> CreativeModeTab.builder(
              CreativeModeTab.Row.TOP, 1)
          .icon(() -> new ItemStack(ModItems.ITEM_BLOCK_BIT.get()))
          .title(Component.literal("Block bits"))
          .displayItems((params, output) -> {
            for (Block block : BuiltInRegistries.BLOCK) {
              if (block instanceof BlockChiseled) {
                continue;
              }
              Set<Integer> reservedStates = new HashSet<>();
              for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
                if (BlockBitInfo.canChisel(possibleState)) {
                  int stateId = ModUtil.getStateId(possibleState);
                  ItemStack itemStack = ItemChiseledBit.createStack(stateId, 1, true);
                  output.accept(itemStack);
                  reservedStates.add(stateId);
                }
              }

              BlockState blockState = block.defaultBlockState();

              if (block instanceof LiquidBlock liquidBlock) {
                Fluid fluid = liquidBlock.getFluidState(blockState).getType();
                if (fluid instanceof FlowingFluid flowingFluid) {
                  blockState =
                      flowingFluid.getSource().defaultFluidState().createLegacyBlock();
                }
              }

              if (BlockBitInfo.canChisel(blockState)) {
                int stateId = ModUtil.getStateId(blockState);
                if (reservedStates.contains(stateId)) {
                  continue;
                }
                ItemStack itemStack = ItemChiseledBit.createStack(stateId, 1, true);
                output.accept(itemStack);
              }
            }
          })
          .build());
  public static Supplier<CreativeModeTab> CLIPBOARD =
      Suppliers.memoize(() -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
          .icon(() -> new ItemStack(ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get()))
          .title(Component.literal("Clipboard"))
          .displayItems((parameters, output) -> {
            output.acceptAll(CreativeClipboardTab.getInstance().getClipboard());
          })
          .build());

  private ModItemGroups() {
    throw new IllegalStateException(
        "Tried to initialize: ModItemGroups but this is a Utility class.");
  }

  public static void onModConstruction() {
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(Constants.MOD_ID, "main_tab"),
        MAIN_TAB.get());
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(Constants.MOD_ID, "block_bits"),
        BLOCK_BITS.get());
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(Constants.MOD_ID, "clipboard"),
        CLIPBOARD.get());
  }
}
