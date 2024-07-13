package mod.chiselsandbits.utils;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;

public final class RenderTypeUtils {

    private RenderTypeUtils() {}

    public static boolean canRenderInLayer(FluidState fluidState, RenderType renderType) {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }
}
