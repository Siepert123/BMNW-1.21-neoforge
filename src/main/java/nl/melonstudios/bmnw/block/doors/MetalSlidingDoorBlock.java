package nl.melonstudios.bmnw.block.doors;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.MetalSlidingDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.init.BMNWStateProperties;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

public class MetalSlidingDoorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final MapCodec<MetalSlidingDoorBlock> CODEC = simpleCodec(MetalSlidingDoorBlock::new);

    public static final BooleanProperty OPEN = BMNWStateProperties.OPEN;
    public static final BooleanProperty MIRRORED = BMNWStateProperties.MIRRORED;
    public static final BooleanProperty UPPER_HALF = BMNWStateProperties.UPPER_HALF;

    protected static final float AABB_DOOR_THICKNESS = 4.0F;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, AABB_DOOR_THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 16.0 - AABB_DOOR_THICKNESS, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(16.0 - AABB_DOOR_THICKNESS, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, AABB_DOOR_THICKNESS, 16.0, 16.0);

    public static final VoxelShape SOUTH_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 0, 1, 16, AABB_DOOR_THICKNESS),
            Block.box(15, 0, 0, 16, 16, AABB_DOOR_THICKNESS)
    );
    public static final VoxelShape NORTH_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 16 - AABB_DOOR_THICKNESS, 1, 16, 16),
            Block.box(15, 0, 16 - AABB_DOOR_THICKNESS, 16, 16, 16)
    );
    public static final VoxelShape WEST_AABB_OPEN = Shapes.or(
            Block.box(16 - AABB_DOOR_THICKNESS, 0, 0, 16, 16, 1),
            Block.box(16 - AABB_DOOR_THICKNESS, 0, 15, 16, 16, 16)
    );
    public static final VoxelShape EAST_AABB_OPEN = Shapes.or(
            Block.box(0, 0, 0, AABB_DOOR_THICKNESS, 16, 1),
            Block.box(0, 0, 15, AABB_DOOR_THICKNESS, 16, 16)
    );

    public MetalSlidingDoorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return switch (state.getValue(FACING)) {
                case SOUTH -> SOUTH_AABB_OPEN;
                case NORTH -> NORTH_AABB_OPEN;
                case WEST -> WEST_AABB_OPEN;
                case EAST -> EAST_AABB_OPEN;
                default -> Shapes.block();
            };
        }
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_AABB;
            case NORTH -> NORTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            default -> Shapes.block();
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(OPEN);
        builder.add(MIRRORED);
        builder.add(UPPER_HALF);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(UPPER_HALF) ? null : new MetalSlidingDoorBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(UPPER_HALF, true), 3);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.METAL_SLIDING_DOOR.get() ? (l, p, s, be) -> ((ITickable)be).update() : null;
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
            Vec3 location = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            Direction dir = context.getHorizontalDirection();
            return this.defaultBlockState()
                    .setValue(FACING, dir)
                    .setValue(OPEN, false)
                    .setValue(UPPER_HALF, false)
                    .setValue(MIRRORED, dir.getAxis().choose(location.z, 1, location.x) < 0.5);
        }
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        boolean upper = state.getValue(UPPER_HALF);

        if (upper) {
            if (!level.getBlockState(pos.below()).is(this)) level.destroyBlock(pos, false);
        } else {
            if (!level.getBlockState(pos.above()).is(this)) level.destroyBlock(pos, false);
            else if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) level.destroyBlock(pos, true);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.setOpen(level, pos, state, !state.getValue(OPEN)) ?
                InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    public boolean setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        if (state.getValue(OPEN) == open) return false;
        boolean upper = state.getValue(UPPER_HALF);
        BlockPos lowerPos = upper ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(lowerPos);
        if (be instanceof MetalSlidingDoorBlockEntity door && !door.canSwitchState()) return false;
        if (level.isClientSide) return true;

        state = state.setValue(OPEN, open);
        level.setBlock(pos, state, 10);
        MetalLockableDoorBlock.playDoorSound(level, pos, state, BMNWSounds.METAL_SLIDING_DOOR.get());

        BlockPos otherPos = upper ? pos.below() : pos.above();
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.is(this)) {
            level.setBlock(otherPos, otherState.setValue(OPEN, open), 10);
        }

        if (be instanceof MetalSlidingDoorBlockEntity door) door.setOpen(open);

        return true;
    }
}
