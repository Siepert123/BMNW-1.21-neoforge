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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

public class CatwalkBlock extends Block implements SimpleWaterloggedBlock, IScrewdriverUsable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty SUPPORT = BooleanProperty.create("support");
    public CatwalkBlock(Properties properties) {
        super(properties.noOcclusion());

        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(AXIS, Direction.Axis.X)
                .setValue(SUPPORT, false));
    }

    public static final VoxelShape SHAPE = box(0, 14, 0, 16, 16, 16);

    public static final VoxelShape SHAPE_SUPPORT = Shapes.or(
            SHAPE,
            QuadPoleBlock.SHAPE
    );

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(SUPPORT) ? SHAPE_SUPPORT : SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
        builder.add(AXIS);
        builder.add(SUPPORT);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN ? state.setValue(SUPPORT, isSuitableSupport(level, pos, true)) : state;
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
        Direction.Axis axis = getPlacementAxis(context.getLevel(), context.getClickedPos(), context.getHorizontalDirection(), context.getClickedFace());
        boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return this.defaultBlockState()
                .setValue(SUPPORT, isSuitableSupport(context.getLevel(), context.getClickedPos(), true))
                .setValue(AXIS, sneaking ? Library.toggleAxis(axis, Direction.Axis.Y) : axis)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    public static boolean isSuitableSupport(LevelAccessor level, BlockPos pos, boolean andSolid) {
        BlockState state = level.getBlockState(pos.below());
        return state.getBlock() instanceof QuadPoleBlock || (andSolid && state.isFaceSturdy(level, pos.below(), Direction.UP));
    }

    private static Direction.Axis getPlacementAxis(Level level, BlockPos pos, Direction horizontal, Direction clicked) {
        int xCounter = 0;
        int zCounter = 0;
        for (int i = 0; i < 4; i++) {
            Direction d = Direction.from2DDataValue(i);
            if (level.getBlockState(pos.relative(d)).getBlock() instanceof CatwalkBlock) {
                if (level.getBlockState(pos.relative(d)).getValue(AXIS) == d.getAxis()) {
                    if (d.getAxis() == Direction.Axis.X) xCounter++;
                    else zCounter++;
                }
            }
        }
        if (xCounter != zCounter) {
            if (xCounter > zCounter) return Direction.Axis.X;
            else return Direction.Axis.Z;
        }
        if (clicked.getAxis() == Direction.Axis.Y) return horizontal.getAxis();
        return clicked.getAxis();
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
            if (isSuitableSupport(level, pos, true)) {
                level.setBlock(pos, state.setValue(SUPPORT, true), 3);
                return true;
            }
        }
        return false;
    }
}
