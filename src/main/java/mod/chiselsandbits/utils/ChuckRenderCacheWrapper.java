package mod.chiselsandbits.utils;

import java.util.function.Supplier;
import mod.chiselsandbits.core.Log;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChuckRenderCacheWrapper implements BlockAndTintGetter {
    private final RenderChunkRegion chunkRenderCache;

    public ChuckRenderCacheWrapper(final RenderChunkRegion chunkRenderCache) {
        this.chunkRenderCache = chunkRenderCache;
    }

    @Override
    public float getShade(final Direction p_230487_1_, final boolean p_230487_2_) {
        return chunkRenderCache.getShade(p_230487_1_, p_230487_2_);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return chunkRenderCache.getLightEngine();
    }

    @Override
    public int getBlockTint(final BlockPos blockPosIn, final ColorResolver colorResolverIn) {
        return this.whenPosValidOrElse(
                blockPosIn, () -> chunkRenderCache.getBlockTint(blockPosIn, colorResolverIn), () -> 0);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return this.whenPosValidOrElse(pos, () -> chunkRenderCache.getBlockEntity(pos), () -> null);
    }

    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return this.whenPosValidOrElse(pos, () -> chunkRenderCache.getBlockState(pos), Blocks.AIR::defaultBlockState);
    }

    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return this.whenPosValidOrElse(pos, () -> chunkRenderCache.getFluidState(pos), Fluids.EMPTY::defaultFluidState);
    }

    private boolean falseWhenInvalidPos(final BlockPos pos, final Supplier<Boolean> validSupplier) {
        return this.whenPosValidOrElse(pos, validSupplier, () -> false);
    }

    private <T> T whenPosValidOrElse(
            final BlockPos pos, final Supplier<T> validSupplier, final Supplier<T> invalidSupplier) {
        if (pos.getY() < 0 || pos.getY() > 255) {
            return invalidSupplier.get();
        }

        try {
            return validSupplier.get();
        } catch (Exception e) {
            Log.logError("Failed to process cached wrapped info for: " + pos, e);
            return invalidSupplier.get();
        }
    }
}
