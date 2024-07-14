package mod.chiselsandbits.registry;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public final class ModItemGroups {

    private ModItemGroups() {
        throw new IllegalStateException("Tried to initialize: ModItemGroups but this is a Utility class.");
    }

    public static final LazyRegistrar<CreativeModeTab> CREATIVE_MOD_TAB =
            LazyRegistrar.create(BuiltInRegistries.CREATIVE_MODE_TAB, ChiselsAndBits.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB =
            CREATIVE_MOD_TAB.register("main_tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
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

    public static final RegistryObject<CreativeModeTab> BLOCK_BITS =
            CREATIVE_MOD_TAB.register("block_bits", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .icon(() -> new ItemStack(ModItems.ITEM_BLOCK_BIT.get()))
                    .title(Component.literal("Block bits"))
                    .displayItems((params, output) -> {
                        for (Block block : BuiltInRegistries.BLOCK) {
                            if (block instanceof BlockChiseled) continue;

                            for (BlockState possibleState :
                                    block.getStateDefinition().getPossibleStates()) {
                                if (BlockBitInfo.canChisel(possibleState)) {
                                    ItemStack itemStack =
                                            ItemChiseledBit.createStack(ModUtil.getStateId(possibleState), 1, true);
                                    output.accept(itemStack);
                                }
                            }

                            BlockState blockState = block.defaultBlockState();

                            if (block instanceof LiquidBlock liquidBlock) {
                                Fluid fluid =
                                        liquidBlock.getFluidState(blockState).getType();
                                if (fluid instanceof FlowingFluid flowingFluid) {
                                    blockState = flowingFluid
                                            .getSource()
                                            .defaultFluidState()
                                            .createLegacyBlock();
                                }
                            }

                            if (BlockBitInfo.canChisel(blockState)) {
                                ItemStack itemStack =
                                        ItemChiseledBit.createStack(ModUtil.getStateId(blockState), 1, true);
                                output.accept(itemStack);
                            }
                        }
                    })
                    .build());

    public static RegistryObject<CreativeModeTab> CLIPBOARD =
            CREATIVE_MOD_TAB.register("clipboard", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .icon(() -> new ItemStack(ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get()))
                    .title(Component.literal("Clipboard"))
                    .displayItems((parameters, output) -> {
                        output.acceptAll(CreativeClipboardTab.getInstance().getClipboard());
                    })
                    .build());
}
