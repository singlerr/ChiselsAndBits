package mod.chiselsandbits.utils;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import mod.chiselsandbits.render.chiseledblock.ChiselRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class RenderTypeUtils {

    public static final Set<ChiselRenderType> TYPE_UNDER_TRANSLUCENT =
            ImmutableSet.of(ChiselRenderType.SOLID, ChiselRenderType.CUTOUT_MIPPED);

    public static final Set<ChiselRenderType> TYPE_UNDER_CUTOUT_MIPPED = ImmutableSet.of(ChiselRenderType.SOLID);

    private RenderTypeUtils() {}

    public static boolean canRenderInLayer(FluidState fluidState, RenderType renderType) {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    public static boolean canRenderInLayer(BlockState fluidState, RenderType renderType) {
        return ItemBlockRenderTypes.getChunkRenderType(fluidState) == renderType;
    }

    public static ChiselRenderType getHighestLayer(Set<ChiselRenderType> renderTypes) {
        ChiselRenderType result = ChiselRenderType.SOLID;
        int priority = 0;
        for (ChiselRenderType renderType : renderTypes) {
            RenderTypePriority p = RenderTypePriority.from(renderType);
            if (p.getPriority() > priority) {
                result = p.getType();
            }
        }

        return result;
    }

    private enum RenderTypePriority {
        SOLID(ChiselRenderType.SOLID, 0),
        SOLID_FLUID(ChiselRenderType.SOLID_FLUID, 0),
        CUTOUT(ChiselRenderType.CUTOUT, 1),
        CUTOUT_MIPPED(ChiselRenderType.CUTOUT_MIPPED, 2),
        TRANSLUCENT(ChiselRenderType.TRANSLUCENT, 3),
        TRANSLUCENT_FLUID(ChiselRenderType.TRANSLUCENT_FLUID, 3),
        TRIPWIRE(ChiselRenderType.TRIPWIRE, 4);

        private final int priority;
        private final ChiselRenderType type;

        RenderTypePriority(ChiselRenderType type, int priority) {
            this.type = type;
            this.priority = priority;
        }

        public static RenderTypePriority from(ChiselRenderType renderType) {
            return switch (renderType) {
                case SOLID -> SOLID;
                case CUTOUT_MIPPED -> CUTOUT_MIPPED;
                case CUTOUT -> CUTOUT;
                case TRIPWIRE -> TRIPWIRE;
                case SOLID_FLUID -> SOLID_FLUID;
                case TRANSLUCENT -> TRANSLUCENT;
                case TRANSLUCENT_FLUID -> TRANSLUCENT_FLUID;
            };
        }

        public ChiselRenderType getType() {
            return type;
        }

        public int getPriority() {
            return priority;
        }
    }
}
