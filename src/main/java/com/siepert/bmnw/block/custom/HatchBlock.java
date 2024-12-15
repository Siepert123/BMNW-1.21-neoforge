package com.siepert.bmnw.block.custom;

import com.mojang.serialization.MapCodec;
import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.block.entity.custom.HatchBlockEntity;
import com.siepert.bmnw.misc.BMNWSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HatchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape CLOSED_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);

    public HatchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
        //FIXME: replace with actual values
        open = BMNWSounds.GEIGER_CLICK.get();
        close = BMNWSounds.SMALL_EXPLOSION.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(OPEN);
    }

    private final SoundEvent open, close;

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(OPEN)) {
            return CLOSED_AABB;
        } else {
            return switch (state.getValue(FACING)) {
                default -> NORTH_OPEN_AABB;
                case SOUTH -> SOUTH_OPEN_AABB;
                case WEST -> WEST_OPEN_AABB;
                case EAST -> EAST_OPEN_AABB;
            };
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        toggle(state, level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void toggle(BlockState state, Level level, BlockPos pos, Player player) {
        BlockState blockState = state.cycle(OPEN);
        level.setBlock(pos, blockState, 2);

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof HatchBlockEntity hatch) {
            hatch.ticks = 20;
            hatch.open = state.getValue(OPEN);
        }

        this.playSound(player, level, pos, blockState.getValue(OPEN));
    }
    private void playSound(@Nullable Player player, Level level, BlockPos pos, boolean opened) {
        level.playSound(
                player, pos, opened ? open : close,
                SoundSource.BLOCKS, 1.0f, level.getRandom().nextFloat() * 0.1f + 0.95f
        );
        level.gameEvent(player, opened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        Direction direction = context.getClickedFace();
        if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            return blockstate.setValue(FACING, direction);
        } else {
            return blockstate.setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HatchBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.HATCH.get() ? HatchBlockEntity::tick : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
