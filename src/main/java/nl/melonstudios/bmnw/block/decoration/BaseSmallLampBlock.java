package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.entity.SmallLampBlockEntity;
import nl.melonstudios.bmnw.misc.PartialModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseSmallLampBlock extends Block implements EntityBlock {
    public static final ArrayList<BaseSmallLampBlock> LAMP_BLOCKS = new ArrayList<>();
    public final boolean inverted;
    public final DyeColor color;
    public BaseSmallLampBlock(Properties properties, boolean inverted, DyeColor color) {
        super(properties.noOcclusion());
        this.inverted = inverted;
        this.color = color;

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(POWERED, false)
                .setValue(DYEABLE, true));

        VoxelShape[] shapes = this.getShapeArray();
        if (shapes.length != 6) throw new IllegalStateException("Small lamp must have 6 voxel shapes!!");
        this.shapes = shapes;

        LAMP_BLOCKS.add(this);
    }

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty DYEABLE = BooleanProperty.create("dyeable");

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        boolean powered = level.hasNeighborSignal(pos);
        if (state.getValue(POWERED) != powered) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
        }
    }

    @Nonnull
    protected abstract VoxelShape[] getShapeArray();

    private final VoxelShape[] shapes;

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapes[state.getValue(FACING).get3DDataValue()];
    }

    @Override
    protected final int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }
    @Override
    protected final float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public final int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(POWERED) ? (this.inverted ? 0 : 15) : (this.inverted ? 15 : 0);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        BlockState initialState = this.defaultBlockState().setValue(FACING, facing);
        return this.canSurvive(initialState, level, pos) ? initialState.setValue(POWERED, level.hasNeighborSignal(pos)) : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return level.getBlockState(pos.relative(facing.getOpposite())).isFaceSturdy(level, pos.relative(facing.getOpposite()), facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(DYEABLE);
    }


    @OnlyIn(Dist.CLIENT)
    public boolean renderGlow(BlockState state) {
        return state.getValue(POWERED) ^ this.inverted;
    }
    @OnlyIn(Dist.CLIENT)
    public abstract PartialModel getLampPart();

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmallLampBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }
}
