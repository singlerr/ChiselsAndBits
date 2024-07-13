package mod.chiselsandbits.printer;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChiselPrinterBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Map<Direction, VoxelShape> BUTTON_VS_MAP = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.NORTH, Block.box(7, 1, -0.5, 12, 4, 0))
            .put(Direction.EAST, Block.box(16, 1, 7, 16.5, 4, 12))
            .put(Direction.SOUTH, Block.box(4, 1, 16, 9, 4, 16.5))
            .put(Direction.WEST, Block.box(-0.5, 1, 4, 0, 4, 9))
            .build();

    private static final VoxelShape VS_NORTH = createForDirection(Direction.NORTH);
    private static final VoxelShape VS_EAST = createForDirection(Direction.EAST);
    private static final VoxelShape VS_SOUTH = createForDirection(Direction.SOUTH);
    private static final VoxelShape VS_WEST = createForDirection(Direction.WEST);

    private static final Map<Direction, VoxelShape> VS_MAP = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.NORTH, VS_NORTH)
            .put(Direction.EAST, VS_EAST)
            .put(Direction.SOUTH, VS_SOUTH)
            .put(Direction.WEST, VS_WEST)
            .build();

    private static VoxelShape createForDirection(final Direction direction) {
        return Shapes.join(
                Shapes.join(
                        Stream.of(
                                        Block.box(0, 5, 0, 2, 16, 2),
                                        Block.box(14, 5, 0, 16, 16, 2),
                                        Block.box(0, 5, 14, 2, 16, 16),
                                        Block.box(14, 5, 14, 16, 16, 16))
                                .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
                                .orElse(Shapes.empty()),
                        Stream.of(
                                        Block.box(2, 14, 0, 14, 16, 2),
                                        Block.box(2, 14, 14, 14, 16, 16),
                                        Block.box(0, 14, 2, 2, 16, 14),
                                        Block.box(14, 14, 2, 16, 16, 14),
                                        Stream.of(
                                                        Block.box(2, 14, 7, 14, 16, 9),
                                                        Block.box(7, 13.99, 2, 9, 15.98, 14),
                                                        Block.box(7, 11, 7, 9, 14, 9),
                                                        Block.box(7.5, 10, 7.5, 8.5, 11, 8.5))
                                                .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
                                                .orElse(Shapes.empty()))
                                .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
                                .orElse(Shapes.empty()),
                        BooleanOp.OR),
                Shapes.join(
                        Block.box(0, 0, 0, 16, 5, 16),
                        BUTTON_VS_MAP.getOrDefault(direction, Shapes.empty()),
                        BooleanOp.OR),
                BooleanOp.OR);
    }

    public ChiselPrinterBlock(final Properties builder) {
        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(
            final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext context) {
        return VS_MAP.getOrDefault(state.getValue(FACING), Shapes.empty());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockGetter worldIn) {
        return new ChiselPrinterTileEntity();
    }

    public InteractionResult use(
            BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu((MenuProvider) worldIn.getBlockEntity(pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void appendHoverText(
            final ItemStack stack,
            @Nullable final BlockGetter worldIn,
            final List<Component> tooltip,
            final TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChiselsAndBits.getConfig().getCommon().helpText(LocalStrings.ChiselStationHelp, tooltip);
    }
}
