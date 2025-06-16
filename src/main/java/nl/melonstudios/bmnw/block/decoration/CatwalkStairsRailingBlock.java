package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CatwalkStairsRailingBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape NORTH = Shapes.or(
            box(0, 4-16, 0, 2, 24-16, 8),
            box(14, 4-16, 0, 16, 24-16, 8),
            box(0, 12-16, 8, 2, 32-16, 16),
            box(14, 12-16, 8, 16, 32-16, 16)
    );
    public static final VoxelShape SOUTH = Shapes.or(
            box(0, 4-16, 8, 2, 24-16, 16),
            box(14, 4-16, 8, 16, 24-16, 16),
            box(0, 12-16, 0, 2, 32-16, 8),
            box(14, 12-16, 0, 16, 32-16, 8)
    );
    public static final VoxelShape EAST = Shapes.or(
            box(8, 4-16, 0, 16, 24-16, 2),
            box(8, 4-16, 14, 16, 24-16, 16),
            box(0, 12-16, 0, 8, 32-16, 2),
            box(0, 12-16, 14, 8, 32-16, 16)
    );
    public static final VoxelShape WEST = Shapes.or(
            box(0, 4-16, 0, 8, 24-16, 2),
            box(0, 4-16, 14, 8, 24-16, 16),
            box(8, 12-16, 0, 16, 32-16, 2),
            box(8, 12-16, 14, 16, 32-16, 16)
    );

    public static final VoxelShape COL_NORTH = Shapes.or(
            box(0, 4-16, 0, 2, 24-8, 8),
            box(14, 4-16, 0, 16, 24-8, 8),
            box(0, 12-16, 8, 2, 32-8, 16),
            box(14, 12-16, 8, 16, 32-8, 16)
    );
    public static final VoxelShape COL_SOUTH = Shapes.or(
            box(0, 4-16, 8, 2, 24-8, 16),
            box(14, 4-16, 8, 16, 24-8, 16),
            box(0, 12-16, 0, 2, 32-8, 8),
            box(14, 12-16, 0, 16, 32-8, 8)
    );
    public static final VoxelShape COL_EAST = Shapes.or(
            box(8, 4-16, 0, 16, 24-8, 2),
            box(8, 4-16, 14, 16, 24-8, 16),
            box(0, 12-16, 0, 8, 32-8, 2),
            box(0, 12-16, 14, 8, 32-8, 16)
    );
    public static final VoxelShape COL_WEST = Shapes.or(
            box(0, 4-16, 0, 8, 24-8, 2),
            box(0, 4-16, 14, 8, 24-8, 16),
            box(8, 12-16, 0, 16, 32-8, 2),
            box(8, 12-16, 14, 16, 32-8, 16)
    );

    public CatwalkStairsRailingBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> Shapes.block();
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> COL_NORTH;
            case SOUTH -> COL_SOUTH;
            case EAST -> COL_EAST;
            case WEST -> COL_WEST;
            default -> Shapes.block();
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(WATERLOGGED);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof CatwalkStairsBlock;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return this.canSurvive(state, level, pos) ? state.setValue(FACING, level.getBlockState(pos.below()).getValue(FACING)) : state;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!this.canSurvive(state, level, pos)) level.destroyBlock(pos, true);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (this.canSurvive(this.defaultBlockState(), context.getLevel(), context.getClickedPos())) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getLevel().getBlockState(context.getClickedPos().below()).getValue(FACING))
                    .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
        }
        return null;
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
        return 1.0F;
    }
}
