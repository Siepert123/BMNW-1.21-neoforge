package nl.melonstudios.bmnw.block.doors;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.decoration.ConcreteEncapsulatedLadderBlock;
import nl.melonstudios.bmnw.block.entity.HatchBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWSounds;

import javax.annotation.Nullable;

public class HatchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    protected static final VoxelShape CLOSED_AABB = Shapes.or(
            ConcreteEncapsulatedLadderBlock.SHAPE,
            BMNW.shape(0, 15, 0, 16, 16, 16)
    );

    public HatchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
        open = BMNWSounds.HATCH_OPEN.get();
        close = BMNWSounds.HATCH_CLOSE.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(OPEN);
    }

    private final SoundEvent open, close;

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return ConcreteEncapsulatedLadderBlock.SHAPE;
        } else {
            return CLOSED_AABB;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (hitResult.getDirection().getAxis() == Direction.Axis.Y) {
            toggle(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else return InteractionResult.PASS;
    }

    private void toggle(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.getBlockState(pos.above()).canOcclude() && !state.getValue(OPEN)) {
            return;
        }
        HatchBlockEntity be = getEntityOrNull(level, pos);
        if (be != null && !be.toggleable()) return;


        BlockState blockState = state.cycle(OPEN);
        level.setBlock(pos, blockState, 2);

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof HatchBlockEntity hatch) {
            hatch.resetAnimTicks();
            hatch.open = blockState.getValue(OPEN);
        }

        this.playSound(player, level, pos, blockState.getValue(OPEN));
    }
    private void playSound(@Nullable Player player, Level level, BlockPos pos, boolean opened) {
        level.playSound(
                player, pos, opened ? open : close,
                SoundSource.BLOCKS, 0.25f, level.getRandom().nextFloat() * 0.1f + 0.95f
        );
        level.gameEvent(player, opened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.getBlockState(pos.above()).canOcclude() && state.getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 2);
            HatchBlockEntity hatch = getEntityOrNull(level, pos);
            if (hatch != null) hatch.open = false;
        }
    }

    @Nullable
    private HatchBlockEntity getEntityOrNull(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof HatchBlockEntity entity ? entity : null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HatchBlockEntity hatch) {
            hatch.open = false;
        }
    }

    private static final MapCodec<HatchBlock> CODEC = simpleCodec(HatchBlock::new);
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HatchBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.HATCH.get() ? (level1, pos1, state1, beType) -> {
            BlockEntity be = level1.getBlockEntity(pos1);
            if (be instanceof HatchBlockEntity hatchBlockEntity) hatchBlockEntity.tick();
        } : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN) ? 0 : 7;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            if (adjacentState.is(BMNWBlocks.CONCRETE_ENCAPSULATED_LADDER.get())) return true;
        }
        return super.skipRendering(state, adjacentState, direction);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return state.getValue(OPEN) ? dir.getAxis() != Direction.Axis.Y : dir != Direction.DOWN;
    }
}
