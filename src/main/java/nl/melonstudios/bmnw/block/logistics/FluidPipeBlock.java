package nl.melonstudios.bmnw.block.logistics;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.CapabilityRegistry;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import nl.melonstudios.bmnw.block.state.PipeConnectionProperty;
import nl.melonstudios.bmnw.enums.PipeConnection;
import nl.melonstudios.bmnw.event.BMNWEventBus;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.logistics.pipes.IPipeNetPropagator;
import nl.melonstudios.bmnw.logistics.pipes.PipeNetManager;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidPipeBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final PipeConnectionProperty UP = BMNWStateProperties.PIPE_UP;
    public static final PipeConnectionProperty DOWN = BMNWStateProperties.PIPE_DOWN;
    public static final PipeConnectionProperty NORTH = BMNWStateProperties.PIPE_NORTH;
    public static final PipeConnectionProperty EAST = BMNWStateProperties.PIPE_EAST;
    public static final PipeConnectionProperty SOUTH = BMNWStateProperties.PIPE_SOUTH;
    public static final PipeConnectionProperty WEST = BMNWStateProperties.PIPE_WEST;

    public static final VoxelShape SHAPE_CENTER = box(5, 5, 5, 11, 11, 11);
    public static final VoxelShape SHAPE_DOWN = Shapes.or(
            box(5, 1, 5, 11, 5, 11),
            box(3, 0, 3, 13, 1, 13)
    );
    public static final VoxelShape SHAPE_UP = Shapes.or(
            box(5, 11, 5, 11, 15, 11),
            box(3, 15, 3, 13, 16, 13)
    );
    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(5, 5, 1, 11, 11, 5),
            box(3, 3, 0, 13, 13, 1)
    );
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(11, 5, 5, 15, 11, 11),
            box(15, 3, 3, 16, 13, 13)
    );
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(5, 5, 11, 11, 11, 15),
            box(3, 3, 15, 13, 13, 16)
    );
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 5, 5, 5, 11, 11),
            box(0, 3, 3, 1, 13, 13)
    );

    public static final Set<Block> ALL_FLUID_PIPES = new HashSet<>() {
        @Override
        public void clear() {
            throw new IllegalStateException("No.");
        }
    };
    public FluidPipeBlock(Properties properties) {
        super(properties.noLootTable());

        ALL_FLUID_PIPES.add(this);

        registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(UP, PipeConnection.FALSE)
                .setValue(DOWN, PipeConnection.FALSE)
                .setValue(NORTH, PipeConnection.FALSE)
                .setValue(EAST, PipeConnection.FALSE)
                .setValue(SOUTH, PipeConnection.FALSE)
                .setValue(WEST, PipeConnection.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).isSourceOfType(Fluids.WATER);
        return this.defaultBlockState().setValue(WATERLOGGED, waterlogged);
    }

    public static PipeConnectionProperty getSideProperty(@Nonnull Direction face) {
        return switch (face) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };
    }

    public PipeConnection getPipeStateConnection(Level level, BlockPos pos, boolean forcedOff, Direction face) {
        if (forcedOff) return PipeConnection.FORCED_FALSE;
        BlockPos relative = pos.relative(face);
        BlockState neighbour = level.getBlockState(relative);
        if (neighbour.is(this)) {
            return PipeConnection.fromBoolean(!neighbour.getValue(getSideProperty(face.getOpposite())).isForcedOff());
        }
        if (neighbour.getBlock() instanceof FluidPipeBlock) return PipeConnection.FALSE;
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                relative, neighbour, null, face.getOpposite());
        return handler != null ? PipeConnection.TRUE : PipeConnection.FALSE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(BMNWItems.SCREWDRIVER)) {
            Direction face = Library.determineBlockPartition3D(hitResult);
            PipeConnectionProperty property = getSideProperty(face);
            if (state.getValue(property).isForcedOff()) {
                BlockState newState = state.setValue(property, this.getPipeStateConnection(level, pos, false, face));
                if (level.setBlock(pos, newState, 3)) {
                    if (!level.isClientSide && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be
                            && level instanceof ServerLevel serverLevel) {
                        PipeNetManager manager = PipeNetManager.get(serverLevel);
                        manager.handleUpdatedPipe(serverLevel, be);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                BlockState newState = state.setValue(property, PipeConnection.FORCED_FALSE);
                if (level.setBlock(pos, newState, 3)) {
                    if (!level.isClientSide && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be
                            && level instanceof ServerLevel serverLevel) {
                        PipeNetManager manager = PipeNetManager.get(serverLevel);
                        manager.handleUpdatedPipe(serverLevel, be);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE_CENTER;
        if (state.getValue(UP).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_UP, BooleanOp.OR);
        if (state.getValue(DOWN).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_DOWN, BooleanOp.OR);
        if (state.getValue(NORTH).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_NORTH, BooleanOp.OR);
        if (state.getValue(EAST).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_EAST, BooleanOp.OR);
        if (state.getValue(SOUTH).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_SOUTH, BooleanOp.OR);
        if (state.getValue(WEST).isConnected()) shape = Shapes.joinUnoptimized(shape, SHAPE_WEST, BooleanOp.OR);
        return shape.optimize();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.is(oldState.getBlock())) return;
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be) {
            PipeNetManager manager = PipeNetManager.get(serverLevel);
            manager.handlePlacedPipe(serverLevel, be, true);
        }
        level.invalidateCapabilities(pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be) {
            PipeNetManager manager = PipeNetManager.get(serverLevel);
            manager.handleRemovedPipe(serverLevel, be);
        }
        level.removeBlockEntity(pos);
        level.invalidateCapabilities(pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        Block newNeighbourBlock = level.getBlockState(neighborPos).getBlock();
        boolean flag = !level.isClientSide && (level.getBlockEntity(neighborPos) instanceof IPipeNetPropagator ||
                BMNWEventBus.doesBlockHaveCapability(newNeighbourBlock, Capabilities.FluidHandler.BLOCK));
        boolean flag2 = !level.isClientSide && (BMNWEventBus.doesBlockHaveCapability(neighborBlock, Capabilities.FluidHandler.BLOCK));

        if ((flag || flag2) && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be && level instanceof ServerLevel serverLevel) {
            PipeNetManager manager = PipeNetManager.get(serverLevel);
            manager.handleUpdatedPipe(serverLevel, be);
        }
    }
}
