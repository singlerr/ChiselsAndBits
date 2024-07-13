package mod.chiselsandbits.registry;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.items.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

public final class ModItems {

    private static final LazyRegistrar<Item> ITEM_REGISTRAR =
            LazyRegistrar.create(Registries.ITEM, ChiselsAndBits.MODID);

    private ModItems() {
        throw new IllegalStateException("Tried to initialize: ModItems but this is a Utility class.");
    }

    public static final RegistryObject<ItemChisel> ITEM_CHISEL_STONE =
            ITEM_REGISTRAR.register("chisel_stone", () -> new ItemChisel(Tiers.STONE, new Item.Properties()));
    public static final RegistryObject<ItemChisel> ITEM_CHISEL_IRON =
            ITEM_REGISTRAR.register("chisel_iron", () -> new ItemChisel(Tiers.IRON, new Item.Properties()));
    public static final RegistryObject<ItemChisel> ITEM_CHISEL_GOLD =
            ITEM_REGISTRAR.register("chisel_gold", () -> new ItemChisel(Tiers.GOLD, new Item.Properties()));
    public static final RegistryObject<ItemChisel> ITEM_CHISEL_DIAMOND =
            ITEM_REGISTRAR.register("chisel_diamond", () -> new ItemChisel(Tiers.DIAMOND, new Item.Properties()));
    public static final RegistryObject<ItemChisel> ITEM_CHISEL_NETHERITE = ITEM_REGISTRAR.register(
            "chisel_netherite", () -> new ItemChisel(Tiers.NETHERITE, new Item.Properties().fireResistant()));

    public static final RegistryObject<ItemChiseledBit> ITEM_BLOCK_BIT =
            ITEM_REGISTRAR.register("block_bit", () -> new ItemChiseledBit(new Item.Properties()));
    public static final RegistryObject<ItemMirrorPrint> ITEM_MIRROR_PRINT =
            ITEM_REGISTRAR.register("mirrorprint", () -> new ItemMirrorPrint(new Item.Properties()));
    public static final RegistryObject<ItemMirrorPrint> ITEM_MIRROR_PRINT_WRITTEN =
            ITEM_REGISTRAR.register("mirrorprint_written", () -> new ItemMirrorPrint(new Item.Properties()));
    public static final RegistryObject<ItemPositivePrint> ITEM_POSITIVE_PRINT =
            ITEM_REGISTRAR.register("positiveprint", () -> new ItemPositivePrint(new Item.Properties()));
    public static final RegistryObject<ItemPositivePrint> ITEM_POSITIVE_PRINT_WRITTEN =
            ITEM_REGISTRAR.register("positiveprint_written", () -> new ItemPositivePrint(new Item.Properties()));
    public static final RegistryObject<ItemNegativePrint> ITEM_NEGATIVE_PRINT =
            ITEM_REGISTRAR.register("negativeprint", () -> new ItemNegativePrint(new Item.Properties()));
    public static final RegistryObject<ItemNegativePrint> ITEM_NEGATIVE_PRINT_WRITTEN =
            ITEM_REGISTRAR.register("negativeprint_written", () -> new ItemNegativePrint(new Item.Properties()));

    public static final RegistryObject<ItemBitBag> ITEM_BIT_BAG_DEFAULT =
            ITEM_REGISTRAR.register("bit_bag", () -> new ItemBitBag(new Item.Properties()));
    public static final RegistryObject<ItemBitBag> ITEM_BIT_BAG_DYED =
            ITEM_REGISTRAR.register("bit_bag_dyed", () -> new ItemBitBag(new Item.Properties()));
    public static final RegistryObject<ItemWrench> ITEM_WRENCH =
            ITEM_REGISTRAR.register("wrench_wood", () -> new ItemWrench(new Item.Properties()));
    public static final RegistryObject<ItemBitSaw> ITEM_BIT_SAW_STONE =
            ITEM_REGISTRAR.register("bitsaw_stone", () -> new ItemBitSaw(Tiers.STONE, new Item.Properties()));
    public static final RegistryObject<ItemBitSaw> ITEM_BIT_SAW_GOLD =
            ITEM_REGISTRAR.register("bitsaw_gold", () -> new ItemBitSaw(Tiers.GOLD, new Item.Properties()));
    public static final RegistryObject<ItemBitSaw> ITEM_BIT_SAW_IRON =
            ITEM_REGISTRAR.register("bitsaw_iron", () -> new ItemBitSaw(Tiers.IRON, new Item.Properties()));
    public static final RegistryObject<ItemBitSaw> ITEM_BIT_SAW_DIAMOND =
            ITEM_REGISTRAR.register("bitsaw_diamond", () -> new ItemBitSaw(Tiers.DIAMOND, new Item.Properties()));
    public static final RegistryObject<ItemBitSaw> ITEM_BIT_SAW_NETHERITE = ITEM_REGISTRAR.register(
            "bitsaw_netherite", () -> new ItemBitSaw(Tiers.NETHERITE, new Item.Properties().fireResistant()));
    public static final RegistryObject<ItemTapeMeasure> ITEM_TAPE_MEASURE =
            ITEM_REGISTRAR.register("tape_measure", () -> new ItemTapeMeasure(new Item.Properties()));
    public static final RegistryObject<ItemMagnifyingGlass> ITEM_MAGNIFYING_GLASS =
            ITEM_REGISTRAR.register("magnifying_glass", () -> new ItemMagnifyingGlass(new Item.Properties()));

    public static void onModConstruction() {
        ITEM_REGISTRAR.register();
    }
}
