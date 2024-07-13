package mod.chiselsandbits.registry;

import com.google.common.collect.Maps;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import mod.chiselsandbits.bitstorage.BlockBitStorage;
import mod.chiselsandbits.bitstorage.ItemBlockBitStorage;
import mod.chiselsandbits.bitstorage.ItemStackSpecialRendererBitStorage;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.MaterialType;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.printer.ChiselPrinterBlock;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public final class ModBlocks {

    private static final LazyRegistrar<Block> BLOCK_REGISTRAR =
            LazyRegistrar.create(Registries.BLOCK, ChiselsAndBits.MODID);
    private static final LazyRegistrar<Item> ITEM_REGISTRAR =
            LazyRegistrar.create(Registries.ITEM, ChiselsAndBits.MODID);

    public static final Map<String, RegistryObject<BlockChiseled>> MATERIAL_TO_BLOCK_CONVERSIONS = Maps.newHashMap();
    public static final Map<String, RegistryObject<ItemBlockChiseled>> MATERIAL_TO_ITEM_CONVERSIONS = Maps.newHashMap();

    public static final RegistryObject<BlockBitStorage> BIT_STORAGE_BLOCK = BLOCK_REGISTRAR.register(
            "bit_storage",
            () -> new BlockBitStorage(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .dynamicShape()
                    .noOcclusion()
                    .isValidSpawn((p_test_1_, p_test_2_, p_test_3_, p_test_4_) -> false)
                    .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)));

    public static final RegistryObject<BlockItem> BIT_STORAGE_BLOCK_ITEM = ITEM_REGISTRAR.register(
            "bit_storage", () -> new ItemBlockBitStorage(BIT_STORAGE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<ChiselPrinterBlock> CHISEL_PRINTER_BLOCK = BLOCK_REGISTRAR.register(
            "chisel_printer",
            () -> new ChiselPrinterBlock(BlockBehaviour.Properties.of()
                    .strength(1.5f, 6f)
                    .noOcclusion()
                    .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)));

    public static final RegistryObject<BlockChiseled> CHISELED_BLOCK = BLOCK_REGISTRAR.register(
            "chiseled_block",
            () -> new BlockChiseled(
                    "chiseled_block",
                    BlockBehaviour.Properties.of()
                            .pushReaction(PushReaction.BLOCK)
                            .strength(1.5f, 6f)
                            .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)
                            .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                            .noOcclusion()));
    public static final RegistryObject<BlockItem> CHISEL_PRINTER_ITEM = ITEM_REGISTRAR.register(
            "chisel_printer", () -> new BlockItem(ModBlocks.CHISEL_PRINTER_BLOCK.get(), new Item.Properties()));

    public static final MaterialType[] VALID_CHISEL_MATERIALS = new MaterialType[] {
        new MaterialType("wood", "wood"),
        new MaterialType("rock", "stone"),
        new MaterialType("iron", "metal"),
        new MaterialType("cloth", "cloth_decoration"),
        new MaterialType("ice", "ice"),
        new MaterialType("packed_ice", "ice_solid"),
        new MaterialType("clay", "clay"),
        new MaterialType("glass", "glass"),
        new MaterialType("sand", "sand"),
        new MaterialType("ground", "dirt"),
        new MaterialType("grass", "dirt"),
        new MaterialType("snow", "snow"),
        new MaterialType("fluid", "water"),
        new MaterialType("leaves", "leaves"),
    };

    private ModBlocks() {
        throw new IllegalStateException("Tried to initialize: ModBlocks but this is a Utility class.");
    }

    public static void onModConstruction() {
        BLOCK_REGISTRAR.register();
        ITEM_REGISTRAR.register();

        Arrays.stream(VALID_CHISEL_MATERIALS).forEach(materialType -> {
            MATERIAL_TO_BLOCK_CONVERSIONS.put(
                    materialType.getType(),
                    BLOCK_REGISTRAR.register(
                            "chiseled" + materialType.getName(),
                            () -> new BlockChiseled(
                                    "chiseled_" + materialType.getName(),
                                    BlockBehaviour.Properties.of()
                                            .pushReaction(PushReaction.BLOCK)
                                            .strength(1.5f, 6f)
                                            .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)
                                            .isRedstoneConductor((p_test_1_, p_test_2_, p_test_3_) -> false)
                                            .noOcclusion())));
            MATERIAL_TO_ITEM_CONVERSIONS.put(
                    materialType.getType(),
                    ITEM_REGISTRAR.register(
                            "chiseled" + materialType.getName(),
                            () -> new ItemBlockChiseled(
                                    MATERIAL_TO_BLOCK_CONVERSIONS
                                            .get(materialType.getType())
                                            .get(),
                                    new Item.Properties())));
        });
        BuiltinItemRendererRegistry.INSTANCE.register(
                BIT_STORAGE_BLOCK_ITEM.get(), new ItemStackSpecialRendererBitStorage()::renderByItem);
    }

    public static Map<String, RegistryObject<ItemBlockChiseled>> getMaterialToItemConversions() {
        return MATERIAL_TO_ITEM_CONVERSIONS;
    }

    public static Map<String, RegistryObject<BlockChiseled>> getMaterialToBlockConversions() {
        return MATERIAL_TO_BLOCK_CONVERSIONS;
    }

    public static MaterialType[] getValidChiselMaterials() {
        return VALID_CHISEL_MATERIALS;
    }

    @Nullable
    public static BlockState getChiseledDefaultState() {
        final Iterator<RegistryObject<BlockChiseled>> blockIterator =
                getMaterialToBlockConversions().values().iterator();
        if (blockIterator.hasNext()) return blockIterator.next().get().defaultBlockState();

        return null;
    }

    public static BlockChiseled convertGivenStateToChiseledBlock(final BlockState state) {

        return CHISELED_BLOCK.get();
    }

    public static RegistryObject<BlockChiseled> convertGivenMaterialToChiseledRegistryBlock() {
        return CHISELED_BLOCK;
    }
}
