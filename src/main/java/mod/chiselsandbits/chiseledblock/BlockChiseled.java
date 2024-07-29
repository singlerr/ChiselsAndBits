package mod.chiselsandbits.chiseledblock;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import javax.annotation.Nonnull;
import mod.chiselsandbits.api.BoxType;
import mod.chiselsandbits.api.ChiseledBlock;
import mod.chiselsandbits.api.IMultiStateBlock;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.chiseledblock.data.VoxelShapeCache;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.BitOperation;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ExceptionNoTileEntity;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.utils.SingleBlockBlockReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockChiseled extends Block
        implements EntityBlock, IMultiStateBlock, IBlockWithWorldlyProperties, ChiseledBlock {

    public static final BlockPos ZERO = BlockPos.ZERO;

    private static ThreadLocal<BlockState> actingAs = new ThreadLocal<BlockState>();

    public static final BooleanProperty FULL_BLOCK = BooleanProperty.create("full_block");

    public final String name;

    public BlockChiseled(final String name, final BlockBehaviour.Properties properties) {
        super(properties);
        this.name = name;
        this.registerDefaultState(this.stateDefinition.any().setValue(FULL_BLOCK, false));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockState newState = super.playerWillDestroy(level, blockPos, blockState, player);
        try {
            final TileEntityBlockChiseled tebc = getTileEntity(level, blockPos);
            if (ChiselsAndBits.getConfig()
                    .getClient()
                    .addBrokenBlocksToCreativeClipboard
                    .get()) {

                CreativeClipboardTab.getInstance().addItem(tebc.getItemStack(player));
            }
            UndoTracker.getInstance()
                    .add(level, blockPos, tebc.getBlobStateReference(), new VoxelBlobStateReference(0, 0));
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
        }
        return newState;
    }

    @Override
    public boolean shouldCheckWeakPower(
            BlockState blockState, SignalGetter signalGetter, BlockPos blockPos, Direction direction) {
        return isFullCube(blockState);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(
            BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, FluidState fluidState) {
        return false;
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState blockState, LevelReader levelReader, BlockPos blockPos, BlockPos blockPos1) {
        return new float[0];
    }

    @Override
    public SoundType getSoundType(
            BlockState blockState, LevelReader levelReader, BlockPos blockPos, @Nullable Entity entity) {
        try {
            TileEntityBlockChiseled te = getTileEntity(levelReader, blockPos);
            int p = te.getPrimaryBlockStateId();

            BlockState s = ModUtil.getStateById(p);
            return s.getSoundType();
        } catch (ExceptionNoTileEntity e) {
            return SoundType.STONE;
        }
    }

    @Override
    public float getExplosionResistance(
            BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Explosion explosion) {
        return 0;
    }

    @Override
    public float getFriction(
            BlockState blockState, LevelReader levelReader, BlockPos blockPos, @Nullable Entity entity) {
        try {
            BlockState internalState = getTileEntity(levelReader, blockPos).getBlockState(Blocks.STONE);

            if (internalState != null) {
                return internalState.getBlock().getFriction();
            }
        } catch (ExceptionNoTileEntity e) {
            Log.noTileError(e);
        }

        return super.getFriction();
    }

    // TODO(This fix stained glass render?)
    @Environment(EnvType.CLIENT)
    @Override
    public float getShadeBrightness(final BlockState state, final BlockGetter worldIn, final BlockPos pos) {
        return isFullCube(state) ? 0.2F : 1F;
    }

    @Override
    public boolean canBeReplaced(final BlockState state, final BlockPlaceContext useContext) {
        try {
            BlockPos target = useContext.getClickedPos();
            if (!(useContext instanceof DirectionalPlaceContext) && !useContext.replacingClickedOnBlock()) {
                target = target.relative(useContext.getClickedFace().getOpposite());
            }

            return getTileEntity(useContext.getLevel(), target).getBlob().filled() == 0;
        } catch (final ExceptionNoTileEntity e) {
            return true;
        }
    }

    static ExceptionNoTileEntity noTileEntity = new ExceptionNoTileEntity();

    public static @Nonnull TileEntityBlockChiseled getTileEntity(final BlockEntity te) throws ExceptionNoTileEntity {
        if (te == null) {
            throw noTileEntity;
        }

        try {
            return (TileEntityBlockChiseled) te;
        } catch (final ClassCastException e) {
            throw noTileEntity;
        }
    }

    public static @Nonnull TileEntityBlockChiseled getTileEntity(
            final @Nonnull BlockGetter world, final @Nonnull BlockPos pos) throws ExceptionNoTileEntity {
        final BlockEntity te = ModUtil.getTileEntitySafely(world, pos);
        return getTileEntity(te);
    }

    @Override
    public int getLightBlock(final BlockState state, final BlockGetter worldIn, final BlockPos pos) {
        return isFullCube(state) ? 0 : 1;
    }

    public static boolean isFullCube(final BlockState state) {
        return state.getValue(FULL_BLOCK);
    }

    @Override
    public void playerDestroy(
            final Level worldIn,
            final Player player,
            final BlockPos pos,
            final BlockState state,
            final BlockEntity te,
            final ItemStack stack) {
        try {
            popResource(worldIn, pos, getTileEntity(te).getItemStack(player));
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            super.playerDestroy(worldIn, player, pos, state, (BlockEntity) null, stack);
        }
    }

    @Override
    public void setPlacedBy(
            final Level worldIn,
            final BlockPos pos,
            final BlockState state,
            @Nullable final LivingEntity placer,
            final ItemStack stack) {
        try {
            if (stack == null || placer == null || !stack.hasTag()) {
                return;
            }

            final TileEntityBlockChiseled bc = getTileEntity(worldIn, pos);
            if (worldIn.isClientSide) {
                bc.getState();
            }
            int rotations = ModUtil.getRotations(placer, ModUtil.getSide(stack));

            VoxelBlob blob = bc.getBlob();
            while (rotations-- > 0) {
                blob = blob.spin(Axis.Y);
            }
            bc.setBlob(blob);
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
        }
    }

    @Override
    public ItemStack getCloneItemStack(
            BlockState blockState, HitResult target, LevelReader levelReader, BlockPos blockPos, Player player) {
        if (!(target instanceof BlockHitResult)) return ItemStack.EMPTY;

        try {
            return getPickBlock((BlockHitResult) target, blockPos, getTileEntity(levelReader, blockPos));
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return ModUtil.getEmptyStack();
        }
    }

    /**
     * Client side method.
     */
    private ChiselToolType getClientHeldTool() {
        return ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND);
    }

    public ItemStack getPickBlock(final BlockHitResult target, final BlockPos pos, final TileEntityBlockChiseled te) {
        if (te.getLevel().isClientSide()) {
            if (getClientHeldTool() != null) {
                final VoxelBlob vb = te.getBlob();

                final BitLocation bitLoc = new BitLocation(target, BitOperation.CHISEL);

                final int itemBlock = vb.get(bitLoc.bitX, bitLoc.bitY, bitLoc.bitZ);
                if (itemBlock == 0) {
                    return ModUtil.getEmptyStack();
                }

                return ItemChiseledBit.createStack(itemBlock, 1, false);
            }

            return te.getItemStack(ClientSide.instance.getPlayer());
        }

        return te.getItemStack(null);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FULL_BLOCK);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityBlockChiseled(blockPos, blockState);
    }

    @Override
    public VoxelShape getShape(
            final BlockState state, final BlockGetter reader, final BlockPos pos, final CollisionContext context) {
        try {
            final VoxelBlob blob = getTileEntity(reader, pos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.OCCLUSION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        try {
            final VoxelBlob blob = getTileEntity(worldIn, pos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.COLLISION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        try {
            final VoxelBlob blob = getTileEntity(reader, pos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.OCCLUSION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public static boolean replaceWithChiseled(
            final Level world, final BlockPos pos, final BlockState originalState, final boolean triggerUpdate) {
        return replaceWithChiseled(world, pos, originalState, 0, triggerUpdate).success;
    }

    @Override
    public BlockState rotate(
            final BlockState state, final LevelAccessor world, final BlockPos pos, final Rotation direction) {
        try {
            getTileEntity(world, pos).rotate(direction);
            return state;
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return state;
        }
    }

    public static class ReplaceWithChiseledValue {
        public boolean success = false;
        public TileEntityBlockChiseled te = null;
    }
    ;

    public static ReplaceWithChiseledValue replaceWithChiseled(
            final @Nonnull Level world,
            final @Nonnull BlockPos pos,
            final BlockState originalState,
            final int fragmentBlockStateID,
            final boolean triggerUpdate) {
        BlockState actingState = originalState;
        Block target = originalState.getBlock();
        final boolean isAir = world.isEmptyBlock(pos)
                || actingState.canBeReplaced(
                        new DirectionalPlaceContext(world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
        ReplaceWithChiseledValue rv = new ReplaceWithChiseledValue();

        if (BlockBitInfo.canChisel(actingState) || isAir) {
            BlockChiseled blk = ModBlocks.convertGivenStateToChiseledBlock(originalState);

            int BlockID = ModUtil.getStateId(actingState);

            if (isAir) {
                actingState = ModUtil.getStateById(fragmentBlockStateID);
                target = actingState.getBlock();
                BlockID = ModUtil.getStateId(actingState);
                blk = ModBlocks.convertGivenStateToChiseledBlock(actingState);
                // its still air tho..
                actingState = Blocks.AIR.defaultBlockState();
            }

            if (BlockID == 0) {
                return rv;
            }

            if (blk != null && blk != target) {
                TileEntityBlockChiseled.setLightFromBlock(actingState);
                world.setBlock(pos, blk.defaultBlockState(), triggerUpdate ? 3 : 0);
                TileEntityBlockChiseled.setLightFromBlock(null);
                final BlockEntity te = world.getBlockEntity(pos);

                TileEntityBlockChiseled tec;
                if (!(te instanceof TileEntityBlockChiseled)) {
                    tec = (TileEntityBlockChiseled) blk.newBlockEntity(pos, blk.defaultBlockState());
                    world.setBlockEntity(tec);
                } else {
                    tec = (TileEntityBlockChiseled) te;
                }

                if (tec != null) {
                    tec.fillWith(actingState);
                    tec.setPrimaryBlockStateId(BlockID);
                    tec.setState(tec.getState(), tec.getBlobStateReference());
                }

                rv.success = true;
                rv.te = tec;

                return rv;
            }
        }

        return rv;
    }

    public BlockState getCommonState(final TileEntityBlockChiseled te) {
        final VoxelBlobStateReference data = te.getBlobStateReference();

        if (data != null) {
            final VoxelBlob vb = data.getVoxelBlob();
            if (vb != null) {
                return ModUtil.getStateById(vb.getVoxelStats().mostCommonState);
            }
        }

        return null;
    }

    @Override
    public int getLightEmission(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // is this the right block?
        final BlockState realState = blockGetter.getBlockState(blockPos);
        final Block realBlock = realState.getBlock();
        if (realBlock != this) {
            return 0;
        }

        // enabled?
        if (ChiselsAndBits.getConfig().getServer().enableBitLightSource.get()) {
            try {
                return getTileEntity(blockGetter, blockPos).getLightValue();
            } catch (final ExceptionNoTileEntity e) {
                Log.noTileError(e);
            }
        }

        return 0;
    }

    public static void setActingAs(final BlockState state) {
        actingAs.set(state);
    }

    @Override
    public boolean canHarvestBlock(
            final BlockState state, final BlockGetter world, final BlockPos pos, final Player player) {
        if (ChiselsAndBits.getConfig().getServer().enableToolHarvestLevels.get()) {
            BlockState activeState = actingAs.get();

            if (activeState == null) {
                activeState = getPrimaryState(world, pos);
            }

            return canHarvestBlockInternal(new SingleBlockBlockReader(activeState), pos, player);
        }

        return true;
    }

    private boolean canHarvestBlockInternal(BlockGetter world, BlockPos pos, Player player) {
        BlockState blockState = world.getBlockState(pos);
        Block var6 = blockState.getBlock();
        if (var6 instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.canHarvestBlock(blockState, world, pos, player);
        } else {
            return player.hasCorrectToolForDrops(blockState);
        }
    }

    @Override
    public float getDestroyProgress(
            final BlockState state, final Player player, final BlockGetter worldIn, final BlockPos pos) {
        if (ChiselsAndBits.getConfig().getServer().enableToolHarvestLevels.get()) {
            BlockState actingState = actingAs.get();

            if (actingState == null) {
                actingState = getPrimaryState(worldIn, pos);
            }

            final float hardness = state.getDestroySpeed(worldIn, pos);
            if (hardness < 0.0F) {
                return 0.0F;
            }

            // since we can't call getDigSpeed on the acting state, we can just
            // do some math to try and roughly estimate it.
            float denom = player.inventory.getDestroySpeed(actingState);
            float numer = player.inventory.getDestroySpeed(state);

            if (!canHarvestBlockInternal(new SingleBlockBlockReader(state), ZERO, player)) {
                return player.getDigSpeed(actingState, pos) / hardness / 100F * (numer / denom);
            } else {
                return player.getDigSpeed(actingState, pos) / hardness / 30F * (numer / denom);
            }
        }

        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    public ResourceLocation getModel() {
        return new ResourceLocation(ChiselsAndBits.MODID, name);
    }

    @Override
    public BlockState getPrimaryState(final BlockGetter world, final BlockPos pos) {
        try {
            return getTileEntity(world, pos).getBlockState(Blocks.STONE);
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return Blocks.STONE.defaultBlockState();
        }
    }

    public boolean basicHarvestBlockTest(Level world, BlockPos pos, Player player) {
        return canHarvestBlock(world.getBlockState(pos), world, pos, player);
    }
}
