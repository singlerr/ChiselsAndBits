package mod.chiselsandbits.extensions;

import net.minecraft.core.BlockPos;

/***
 * Store block pos or get block pos
 * @see mod.chiselsandbits.mixin.VoxelShapeMixin
 */
public interface VoxelShapeExtension {

    BlockPos getPos();

    void setPos(BlockPos pos);
}
