package mod.chiselsandbits.mixin;

/***
 * @deprecated
 * Minecraft computes block shapes nearby player to detect collisions with him.
 * However it is important features to the game, but not for ChiselsAndBits.
 * In need of ignoring switching player pose to swimming, it is required to store a pos of a chiseled block to VoxelShape and get in {@link net.minecraft.world.level.CollisionGetter} to apply
 */
public abstract class VoxelShapeMixin {}
