package mod.chiselsandbits.registry;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import mod.chiselsandbits.crafting.BagDyeing;
import mod.chiselsandbits.crafting.BitSawCrafting;
import mod.chiselsandbits.crafting.ChiselBlockCrafting;
import mod.chiselsandbits.crafting.ChiselCrafting;
import mod.chiselsandbits.crafting.MirrorTransferCrafting;
import mod.chiselsandbits.crafting.NegativeInversionCrafting;
import mod.chiselsandbits.crafting.StackableCrafting;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public final class ModRecipeSerializers {

  public static final Supplier<SimpleCraftingRecipeSerializer<BagDyeing>> BAG_DYEING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(BagDyeing::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<ChiselCrafting>> CHISEL_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(ChiselCrafting::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<ChiselBlockCrafting>>
      CHISEL_BLOCK_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(ChiselBlockCrafting::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<StackableCrafting>>
      STACKABLE_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(StackableCrafting::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<NegativeInversionCrafting>>
      NEGATIVE_INVERSION_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(NegativeInversionCrafting::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<MirrorTransferCrafting>>
      MIRROR_TRANSFER_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(MirrorTransferCrafting::new));
  public static final Supplier<SimpleCraftingRecipeSerializer<BitSawCrafting>> BIT_SAW_CRAFTING =
      Suppliers.memoize(() -> new SimpleCraftingRecipeSerializer<>(BitSawCrafting::new));

  private ModRecipeSerializers() {
    throw new IllegalStateException(
        "Tried to initialize: ModRecipeSerializers but this is a Utility class.");
  }

  public static void onModConstruction() {
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "bag_dyeing"),
        BAG_DYEING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "chisel_crafting"),
        CHISEL_CRAFTING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "chisel_block_crafting"),
        CHISEL_BLOCK_CRAFTING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "stackable_crafting"),
        STACKABLE_CRAFTING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "negative_inversion_crafting"),
        NEGATIVE_INVERSION_CRAFTING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "mirror_transfer_crafting"),
        MIRROR_TRANSFER_CRAFTING.get());
    Registry.register(
        BuiltInRegistries.RECIPE_SERIALIZER,
        new ResourceLocation(Constants.MOD_ID, "bit_saw_crafting"),
        BIT_SAW_CRAFTING.get());
  }
}
