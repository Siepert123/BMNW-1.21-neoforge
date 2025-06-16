package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import nl.melonstudios.bmnw.interfaces.IOpensCatwalkRails;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;
import org.jetbrains.annotations.Nullable;

public class CatwalkStairsBlock extends Block implements SimpleWaterloggedBlock, IScrewdriverUsable, IOpensCatwalkRails {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty SUPPORT = CatwalkBlock.SUPPORT;

    public CatwalkStairsBlock(Properties properties) {
        super(properties.noOcclusion());

        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(SUPPORT, false));
    }

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(0, 2, 0, 16, 4, 8),
            box(0, 10, 8, 16, 12, 16)
    );
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(0, 2, 8, 16, 4, 16),
            box(0, 10, 0, 16, 12, 8)
    );
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(8, 2, 0, 16, 4, 16),
            box(0, 10, 0, 8, 12, 16)
    );
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 2, 0, 8, 4, 16),
            box(8, 10, 0, 16, 12, 16)
    );

    public static final VoxelShape SHAPE_NORTH_SUPPORT = Shapes.or(
            SHAPE_NORTH,
            box(2, 0, 2, 14, 2, 8),
            box(2, 0, 8, 14, 10, 14)
    );
    public static final VoxelShape SHAPE_SOUTH_SUPPORT = Shapes.or(
            SHAPE_SOUTH,
            box(2, 0, 8, 14, 2, 14),
            box(2, 0, 2, 14, 10, 8)
    );
    public static final VoxelShape SHAPE_EAST_SUPPORT = Shapes.or(
            SHAPE_EAST,
            box(8, 0, 2, 14, 2, 14),
            box(2, 0, 2, 8, 10, 14)
    );
    public static final VoxelShape SHAPE_WEST_SUPPORT = Shapes.or(
            SHAPE_WEST,
            box(2, 0, 2, 8, 2, 14),
            box(8, 0, 2, 14, 10, 14)
    );

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean support = state.getValue(SUPPORT);
        return switch (state.getValue(FACING)) {
            case NORTH -> support ? SHAPE_NORTH_SUPPORT : SHAPE_NORTH;
            case EAST -> support ? SHAPE_EAST_SUPPORT : SHAPE_EAST;
            case SOUTH -> support ? SHAPE_SOUTH_SUPPORT : SHAPE_SOUTH;
            case WEST -> support ? SHAPE_WEST_SUPPORT : SHAPE_WEST;
            default -> Shapes.block();
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
        builder.add(FACING);
        builder.add(SUPPORT);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(WATERLOGGED) ? 1 : 0;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Direction facing = context.getHorizontalDirection();
        BlockState placedOn = level.getBlockState(pos.relative(face.getOpposite()));
        boolean support = CatwalkBlock.isSuitableSupport(level, pos, false);
        if (face.getAxis() != Direction.Axis.Y && placedOn.getBlock() instanceof CatwalkBlock) {
            return this.defaultBlockState()
                    .setValue(FACING, face)
                    .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                    .setValue(SUPPORT, support);
        }
        return this.defaultBlockState()
                .setValue(FACING, facing.getOpposite())
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                .setValue(SUPPORT, support);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN ? state.setValue(SUPPORT, CatwalkBlock.isSuitableSupport(level, pos, false)) : state;
    }

    @Override
    public boolean onScrewdriverUsed(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.getValue(SUPPORT)) {
            level.setBlock(pos, state.setValue(SUPPORT, false), 3);
            return true;
        } else {
            if (CatwalkBlock.isSuitableSupport(level, pos, false)) {
                level.setBlock(pos, state.setValue(SUPPORT, true), 3);
                return true;
            }
        }
        return false;
    }
}
