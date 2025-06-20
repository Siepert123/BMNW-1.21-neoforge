package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.interfaces.IOpensCatwalkRails;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;
import nl.melonstudios.bmnw.item.tools.ScrewdriverItem;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

public class CatwalkRailingBlock extends Block implements SimpleWaterloggedBlock, IScrewdriverUsable, IOpensCatwalkRails {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape MAIN = Shapes.or(
            box(0, 0, 0, 2, 16, 2),
            box(14, 0, 0, 16, 16, 2),
            box(0, 0, 14, 2, 16, 16),
            box(14, 0, 14, 16, 16, 16)
    );
    public static final VoxelShape SHAPE_NORTH = box(2, 0, 0, 14, 16, 2);
    public static final VoxelShape SHAPE_SOUTH = box(2, 0, 14, 14, 16, 16);
    public static final VoxelShape SHAPE_EAST = box(14, 0, 2, 16, 16, 14);
    public static final VoxelShape SHAPE_WEST = box(0, 0, 2, 2, 16, 14);
    public static final VoxelShape ALL = Shapes.or(
            MAIN, SHAPE_NORTH, SHAPE_SOUTH, SHAPE_EAST, SHAPE_WEST
    );
    public static final VoxelShape COLLISION_MAIN = Shapes.or(
            box(0, 0, 0, 2, 24, 2),
            box(14, 0, 0, 16, 24, 2),
            box(0, 0, 14, 2, 24, 16),
            box(14, 0, 14, 16, 24, 16)
    );
    public static final VoxelShape COLLISION_NORTH = box(2, 0, 0, 14, 24, 2);
    public static final VoxelShape COLLISION_SOUTH = box(2, 0, 14, 14, 24, 16);
    public static final VoxelShape COLLISION_EAST = box(14, 0, 2, 16, 24, 14);
    public static final VoxelShape COLLISION_WEST = box(0, 0, 2, 2, 24, 14);

    public CatwalkRailingBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, SOUTH, EAST, WEST);
        builder.add(WATERLOGGED);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!this.canSurvive(state, level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (ScrewdriverItem.isHoldingScrewdriver(context)) return ALL;
        VoxelShape shape = MAIN;
        if (state.getValue(NORTH)) shape = Shapes.joinUnoptimized(shape, SHAPE_NORTH, BooleanOp.OR);
        if (state.getValue(SOUTH)) shape = Shapes.joinUnoptimized(shape, SHAPE_SOUTH, BooleanOp.OR);
        if (state.getValue(EAST)) shape = Shapes.joinUnoptimized(shape, SHAPE_EAST, BooleanOp.OR);
        if (state.getValue(WEST)) shape = Shapes.joinUnoptimized(shape, SHAPE_WEST, BooleanOp.OR);
        return shape.optimize();
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = COLLISION_MAIN;
        if (state.getValue(NORTH)) shape = Shapes.joinUnoptimized(shape, COLLISION_NORTH, BooleanOp.OR);
        if (state.getValue(SOUTH)) shape = Shapes.joinUnoptimized(shape, COLLISION_SOUTH, BooleanOp.OR);
        if (state.getValue(EAST)) shape = Shapes.joinUnoptimized(shape, COLLISION_EAST, BooleanOp.OR);
        if (state.getValue(WEST)) shape = Shapes.joinUnoptimized(shape, COLLISION_WEST, BooleanOp.OR);
        return shape.optimize();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof ISupportsCatwalkRailing supportsCatwalkRailing && supportsCatwalkRailing.supportsCatwalkRailing(level, pos.below());
    }

    public static BlockState getFittingState(Level level, BlockPos pos, BlockState originalState) {
        originalState = originalState.setValue(NORTH, hasRailingAtSide(level, pos, Direction.NORTH));
        originalState = originalState.setValue(SOUTH, hasRailingAtSide(level, pos, Direction.SOUTH));
        originalState = originalState.setValue(EAST, hasRailingAtSide(level, pos, Direction.EAST));
        originalState = originalState.setValue(WEST, hasRailingAtSide(level, pos, Direction.WEST));
        return originalState;
    }
    public static boolean hasRailingAtSide(Level level, BlockPos pos, Direction side) {
        if (side.getAxis() == Direction.Axis.Y) return false;
        Direction oppositeSide = side.getOpposite();
        BlockState directSide = level.getBlockState(pos.relative(side));
        BlockState directSideUp = level.getBlockState(pos.relative(side).above());
        if (IOpensCatwalkRails.opensCatwalkRails(level, pos.relative(side), oppositeSide, directSide.getBlock())) return false;
        if (directSideUp.isFaceSturdy(level, pos.relative(side).above(), oppositeSide) && directSide.isFaceSturdy(level, pos.relative(side), oppositeSide)) {
            return false;
        }

        BlockState directSideDown = level.getBlockState(pos.relative(side).below());
        return !directSideDown.isFaceSturdy(level, pos.relative(side).below(), Direction.UP) && !(directSideDown.getBlock() instanceof CatwalkStairsBlock);
    }

    @Override
    public boolean onScrewdriverUsed(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            level.setBlock(pos, getFittingState(level, pos, level.getBlockState(pos)), 3);
            return true;
        }
        Direction side = Library.determineBlockPartition2D(context);
        if (side == null) return false;
        BlockState state = level.getBlockState(pos);
        state = switch (side) {
            case NORTH -> state.cycle(NORTH);
            case SOUTH -> state.cycle(SOUTH);
            case EAST -> state.cycle(EAST);
            case WEST -> state.cycle(WEST);
            default -> state;
        };
        return level.setBlock(pos, state, 3);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = getFittingState(context.getLevel(), context.getClickedPos(), this.defaultBlockState()
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER)));
        return this.canSurvive(state, context.getLevel(), context.getClickedPos()) ? state : null;
    }

    public interface ISupportsCatwalkRailing {
        default boolean supportsCatwalkRailing(LevelReader level, BlockPos pos) {
            return true;
        }
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? 1 : 0;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }
}
