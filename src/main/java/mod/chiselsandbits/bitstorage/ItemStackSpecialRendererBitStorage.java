package mod.chiselsandbits.bitstorage;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ItemStackSpecialRendererBitStorage extends BlockEntityWithoutLevelRenderer {
    public ItemStackSpecialRendererBitStorage() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext itemDisplayContext,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay) {
        final BakedModel model = Minecraft.getInstance()
                .getModelManager()
                .getModel(new ModelResourceLocation(ModBlocks.BIT_STORAGE_BLOCK.getId(), "facing=east"));

        Minecraft.getInstance()
                .getBlockRenderer()
                .getModelRenderer()
                .renderModel(
                        matrixStack.last(),
                        buffer.getBuffer(RenderType.translucent()),
                        ModBlocks.BIT_STORAGE_BLOCK.get().defaultBlockState(),
                        model,
                        1f,
                        1f,
                        1f,
                        combinedLight,
                        combinedOverlay);

        final TileEntityBitStorage tileEntity = new TileEntityBitStorage(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
        tileEntity
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .ifPresent(t -> t.fill(
                        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                                .map(s -> s.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                                .orElse(FluidStack.EMPTY),
                        IFluidHandler.FluidAction.EXECUTE));
        Minecraft.getInstance()
                .getBlockEntityRenderDispatcher()
                .renderItem(tileEntity, matrixStack, buffer, combinedLight, combinedOverlay);
    }
}
