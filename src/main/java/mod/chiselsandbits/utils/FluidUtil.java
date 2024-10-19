package mod.chiselsandbits.utils;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public final class FluidUtil {

    private FluidUtil() {}

    public static String getTranslationKey(Fluid fluid) {
        String translationKey;

        if (fluid == Fluids.EMPTY) {
            translationKey = "";
        } else if (fluid == Fluids.WATER) {
            translationKey = "block.minecraft.water";
        } else if (fluid == Fluids.LAVA) {
            translationKey = "block.minecraft.lava";
        } else {
            ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
            String key = Util.makeDescriptionId("block", id);
            String translated = I18n.get(key);
            translationKey = translated.equals(key) ? Util.makeDescriptionId("fluid", id) : key;
        }

        return translationKey;
    }

    public static ResourceLocation getRegistryName(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static int getColor(Fluid fluid) {
        return FluidVariantRendering.getColor(FluidVariant.of(fluid));
    }

    public static TextureAtlasSprite getStillTexture(Fluid fluid) {
        return FluidVariantRendering.getSprites(FluidVariant.of(fluid))[0];
    }

    public static TextureAtlasSprite getFlowingTexture(Fluid fluid) {
        return FluidVariantRendering.getSprites(FluidVariant.of(fluid))[1];
    }

    private static FluidVariant getVariant(Fluid fluid) {
        if (!fluid.isSource(fluid.defaultFluidState()) && fluid != Fluids.EMPTY) {
            return getVariant(getSource(fluid));
        } else {
            return FluidVariant.of(fluid);
        }
    }

    private static Fluid getSource(Fluid fluid) {
        if (fluid instanceof FlowingFluid flowingFluid) {
            return flowingFluid.getSource();
        }
        return fluid;
    }

    public static boolean interactWithFluidHandler(
            @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull IFluidHandler handler) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .map(playerInventory -> {
                        FluidActionResult fluidActionResult =
                                net.minecraftforge.fluids.FluidUtil.tryFillContainerAndStow(
                                        heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                        if (!fluidActionResult.isSuccess()) {
                            fluidActionResult = net.minecraftforge.fluids.FluidUtil.tryEmptyContainerAndStow(
                                    heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                        }

                        if (fluidActionResult.isSuccess()) {
                            player.setItemInHand(hand, fluidActionResult.getResult());
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);
        }
        return false;
    }
}
