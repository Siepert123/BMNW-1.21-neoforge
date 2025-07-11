package nl.melonstudios.bmnw.block.doors;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.SlidingBlastDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlidingBlastDoorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
        public static final BooleanProperty UPPER_HALF = BooleanProperty.create("upper_half");

        protected static final float AABB_DOOR_THICKNESS = 6.0F;
        protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, AABB_DOOR_THICKNESS);
        protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 16.0 - AABB_DOOR_THICKNESS, 16.0, 16.0, 16.0);
        protected static final VoxelShape WEST_AABB = Block.box(16.0 - AABB_DOOR_THICKNESS, 0.0, 0.0, 16.0, 16.0, 16.0);
        protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, AABB_DOOR_THICKNESS, 16.0, 16.0);

    public static final VoxelShape SOUTH_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 0, 0.5, 16, AABB_DOOR_THICKNESS),
            Block.box(15.5, 0, 0, 16, 16, AABB_DOOR_THICKNESS)
    );
    public static final VoxelShape NORTH_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 16 - AABB_DOOR_THICKNESS, 0.5, 16, 16),
            Block.box(15.5, 0, 16 - AABB_DOOR_THICKNESS, 16, 16, 16)
    );
    public static final VoxelShape WEST_AABB_OPEN = Shapes.or(
            Block.box(16 - AABB_DOOR_THICKNESS, 0, 0, 16, 16, 0.5),
            Block.box(16 - AABB_DOOR_THICKNESS, 0, 15.5, 16, 16, 16)
    );
    public static final VoxelShape EAST_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 0, AABB_DOOR_THICKNESS, 16, 0.5),
            Block.box(0, 0, 15.5, AABB_DOOR_THICKNESS, 16, 16)
    );

    public SlidingBlastDoorBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.defaultBlockState()
                .setValue(OPEN, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(UPPER_HALF, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (this.isOpen(state, level, pos)) {
            return switch (state.getValue(FACING)) {
                case NORTH -> NORTH_AABB_OPEN;
                case SOUTH -> SOUTH_AABB_OPEN;
                case EAST -> EAST_AABB_OPEN;
                case WEST -> WEST_AABB_OPEN;
                default -> Shapes.empty();
            };
        }
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            default -> Shapes.empty();
        };
    }

    protected boolean isOpen(BlockState state, BlockGetter level, BlockPos pos) {
        boolean upper = state.getValue(UPPER_HALF);
        BlockPos bePos = upper ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(bePos);
        if (be instanceof SlidingBlastDoorBlockEntity door) return door.mayPass();
        return state.getValue(OPEN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(UPPER_HALF, true), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, FACING, UPPER_HALF);
    }

    private static final MapCodec<SlidingBlastDoorBlock> CODEC = simpleCodec(SlidingBlastDoorBlock::new);
    @Override
    protected MapCodec<SlidingBlastDoorBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(UPPER_HALF) ? null : new SlidingBlastDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.SLIDING_BLAST_DOOR.get() ? ((level1, pos, state1, be) -> {
            ((ITickable)be).update();
        }) : null;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return state.getValue(UPPER_HALF) ? belowState.is(this) : belowState.isFaceSturdy(level, below, Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(OPEN, false)
                    .setValue(UPPER_HALF, false);
        }
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        boolean upper = state.getValue(UPPER_HALF);

        if (upper) {
            if (!level.getBlockState(pos.below()).is(this)) {
                level.destroyBlock(pos, false);
                return;
            }
        } else {
            if (!level.getBlockState(pos.above()).is(this)) {
                level.destroyBlock(pos, false);
                return;
            }
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                level.destroyBlock(pos, true);
                return;
            }
        }

        if (level.hasNeighborSignal(pos)) {
            this.setOpen(level, pos, state, false, true);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.setOpen(level, pos, state, !state.getValue(OPEN), false)) return InteractionResult.sidedSuccess(level.isClientSide);
        return InteractionResult.PASS;
    }

    public boolean setOpen(Level level, BlockPos pos, BlockState state, boolean open, boolean enforce) {
        if (state.getValue(OPEN) == open) return false;
        boolean upper = state.getValue(UPPER_HALF);
        BlockPos lowerPos = upper ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(lowerPos);
        if (!enforce && be instanceof SlidingBlastDoorBlockEntity door && !door.canSwitchState()) return false;
        if (level.isClientSide) return true;
        state = state.setValue(OPEN, open);
        level.setBlock(pos, state, 10);
        if (open) {
            playDoorSound(level, pos, state, BMNWSounds.SLIDING_BLAST_DOOR_OPEN.get());
        } else {
            playDoorSound(level, pos, state, BMNWSounds.SLIDING_BLAST_DOOR_CLOSE.get());
        }

        BlockPos otherPos = upper ? pos.below() : pos.above();
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.is(this)) {
            level.setBlock(otherPos, otherState.setValue(OPEN, open), 10);
        }

        if (be instanceof SlidingBlastDoorBlockEntity door) {
            door.setOpen(open);
        }

        return true;
    }

    public static void playDoorSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        boolean upper = state.getValue(UPPER_HALF);
        double x = pos.getX() + 0.5;
        double y = upper ? pos.getY() : pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS);
    }
}
