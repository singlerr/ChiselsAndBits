package mod.chiselsandbits.registry;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.crafting.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public final class ModRecipeSerializers {

    private static final LazyRegistrar<RecipeSerializer<?>> REGISTRAR =
            LazyRegistrar.create(BuiltInRegistries.RECIPE_SERIALIZER, ChiselsAndBits.MODID);

    private ModRecipeSerializers() {
        throw new IllegalStateException("Tried to initialize: ModRecipeSerializers but this is a Utility class.");
    }

    public static final RegistryObject<SimpleCraftingRecipeSerializer<BagDyeing>> BAG_DYEING =
            REGISTRAR.register("bag_dyeing", () -> new SimpleCraftingRecipeSerializer<>(BagDyeing::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ChiselCrafting>> CHISEL_CRAFTING =
            REGISTRAR.register("chisel_crafting", () -> new SimpleCraftingRecipeSerializer<>(ChiselCrafting::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ChiselBlockCrafting>> CHISEL_BLOCK_CRAFTING =
            REGISTRAR.register(
                    "chisel_block_crafting", () -> new SimpleCraftingRecipeSerializer<>(ChiselBlockCrafting::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<StackableCrafting>> STACKABLE_CRAFTING =
            REGISTRAR.register(
                    "stackable_crafting", () -> new SimpleCraftingRecipeSerializer<>(StackableCrafting::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<NegativeInversionCrafting>>
            NEGATIVE_INVERSION_CRAFTING = REGISTRAR.register(
                    "negative_inversion_crafting",
                    () -> new SimpleCraftingRecipeSerializer<>(NegativeInversionCrafting::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<MirrorTransferCrafting>>
            MIRROR_TRANSFER_CRAFTING = REGISTRAR.register(
                    "mirror_transfer_crafting",
                    () -> new SimpleCraftingRecipeSerializer<>(MirrorTransferCrafting::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<BitSawCrafting>> BIT_SAW_CRAFTING =
            REGISTRAR.register("bit_saw_crafting", () -> new SimpleCraftingRecipeSerializer<>(BitSawCrafting::new));

    public static void onModConstruction() {
        REGISTRAR.register();
    }
}
