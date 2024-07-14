package mod.chiselsandbits.utils;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class RenderTypeUtils {

    private RenderTypeUtils() {}

    public static boolean canRenderInLayer(FluidState fluidState, RenderType renderType) {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    public static boolean canRenderInLayer(BlockState fluidState, RenderType renderType) {
        return ItemBlockRenderTypes.getChunkRenderType(fluidState) == renderType;
    }
}
