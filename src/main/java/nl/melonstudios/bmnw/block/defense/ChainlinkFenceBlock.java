package nl.melonstudios.bmnw.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChainlinkFenceBlock extends Block implements SimpleWaterloggedBlock {
    public static boolean heightenedCollisionBox = false;
    public ChainlinkFenceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PX, false)
                        .setValue(NX, false)
                        .setValue(PZ, false)
                        .setValue(NZ, false)
                        .setValue(WATERLOGGED, false)
        );
    }
    public static final VoxelShape POLE_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
    public static final VoxelShape EAST_SHAPE = Block.box(8, 0, 7.99, 16, 16, 8.01);
    public static final VoxelShape WEST_SHAPE = Block.box(0, 0, 7.99, 8, 16, 8.01);
    public static final VoxelShape SOUTH_SHAPE = Block.box(7.99, 0, 8, 8.01, 16, 16);
    public static final VoxelShape NORTH_SHAPE = Block.box(7.99, 0, 0, 8.01, 16, 8);

    public static final VoxelShape POLE_COLLISION = Block.box(6, 0, 6, 10, 24, 10);
    public static final VoxelShape EAST_COLLISION = Block.box(8, 0, 7.99, 16, 24, 8.01);
    public static final VoxelShape WEST_COLLISION = Block.box(0, 0, 7.99, 8, 24, 8.01);
    public static final VoxelShape SOUTH_COLLISION = Block.box(7.99, 0, 8, 8.01, 24, 16);
    public static final VoxelShape NORTH_COLLISION = Block.box(7.99, 0, 0, 8.01, 24, 8);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final BooleanProperty PX = BlockStateProperties.EAST;
    public static final BooleanProperty NX = BlockStateProperties.WEST;
    public static final BooleanProperty PZ = BlockStateProperties.SOUTH;
    public static final BooleanProperty NZ = BlockStateProperties.NORTH;

    public static final BooleanProperty[] SIDE_LOOKUP = {
            PZ, NX, NZ, PX
    };

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PX, NX, NZ, PZ);
        builder.add(WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape ret = POLE_SHAPE;
        if (state.getValue(PX)) ret = Shapes.or(ret, EAST_SHAPE);
        if (state.getValue(NX)) ret = Shapes.or(ret, WEST_SHAPE);
        if (state.getValue(PZ)) ret = Shapes.or(ret, SOUTH_SHAPE);
        if (state.getValue(NZ)) ret = Shapes.or(ret, NORTH_SHAPE);
        return ret;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (heightenedCollisionBox) {
            VoxelShape ret = POLE_COLLISION;
            if (state.getValue(PX)) ret = Shapes.or(ret, EAST_COLLISION);
            if (state.getValue(NX)) ret = Shapes.or(ret, WEST_COLLISION);
            if (state.getValue(PZ)) ret = Shapes.or(ret, SOUTH_COLLISION);
            if (state.getValue(NZ)) ret = Shapes.or(ret, NORTH_COLLISION);
            return ret;
        } else return this.getShape(state, level, pos, context);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis() == Direction.Axis.Y) return state;
        BooleanProperty property = SIDE_LOOKUP[direction.get2DDataValue()];
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(property, neighborState.is(this) || neighborState.isFaceSturdy(level, pos.relative(direction), direction.getOpposite()));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        for (int i = 0; i < 4; i++) {
            Direction dir = Direction.from2DDataValue(i);
            BlockPos pos = context.getClickedPos().relative(dir);
            if (context.getLevel().getBlockState(pos).isFaceSturdy(context.getLevel(), pos, dir.getOpposite())) {
                state = state.setValue(SIDE_LOOKUP[i], true);
            }
        }
        return state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
