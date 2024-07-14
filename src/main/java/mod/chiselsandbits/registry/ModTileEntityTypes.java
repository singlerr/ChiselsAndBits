package mod.chiselsandbits.registry;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import mod.chiselsandbits.bitstorage.TileEntityBitStorage;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.printer.ChiselPrinterTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModTileEntityTypes {

    private static final LazyRegistrar<BlockEntityType<?>> REGISTRAR =
            LazyRegistrar.create(Registries.BLOCK_ENTITY_TYPE, ChiselsAndBits.MODID);

    private ModTileEntityTypes() {
        throw new IllegalStateException("Tried to initialize: ModTileEntityTypes but this is a Utility class.");
    }

    public static RegistryObject<BlockEntityType<TileEntityBlockChiseled>> CHISELED =
            REGISTRAR.register("chiseled", () -> BlockEntityType.Builder.of(
                            TileEntityBlockChiseled::new,
                            ModBlocks.getMaterialToBlockConversions().values().stream()
                                    .map(RegistryObject::get)
                                    .toArray(Block[]::new))
                    .build(null));

    public static RegistryObject<BlockEntityType<TileEntityBitStorage>> BIT_STORAGE =
            REGISTRAR.register("bit_storage", () -> BlockEntityType.Builder.of(
                            TileEntityBitStorage::new, ModBlocks.BIT_STORAGE_BLOCK.get())
                    .build(null));

    public static RegistryObject<BlockEntityType<ChiselPrinterTileEntity>> CHISEL_PRINTER =
            REGISTRAR.register("chisel_printer", () -> BlockEntityType.Builder.of(
                            ChiselPrinterTileEntity::new, ModBlocks.CHISEL_PRINTER_BLOCK.get())
                    .build(null));

    public static void onModConstruction() {
        REGISTRAR.register();
    }
}
