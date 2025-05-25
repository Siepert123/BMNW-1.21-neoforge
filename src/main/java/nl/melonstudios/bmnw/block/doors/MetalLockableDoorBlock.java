package nl.melonstudios.bmnw.block.doors;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.MetalLockableDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetalLockableDoorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty UPPER_HALF = SlidingBlastDoorBlock.UPPER_HALF;
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");

    protected static final float AABB_DOOR_THICKNESS = 2.0F;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, AABB_DOOR_THICKNESS);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 16.0 - AABB_DOOR_THICKNESS, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(16.0 - AABB_DOOR_THICKNESS, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, AABB_DOOR_THICKNESS, 16.0, 16.0);

    public MetalLockableDoorBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(UPPER_HALF, false)
                .setValue(LOCKED, false)
                .setValue(MIRRORED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(OPEN);
        builder.add(UPPER_HALF);
        builder.add(LOCKED);
        builder.add(MIRRORED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean upper = state.getValue(UPPER_HALF);
        boolean open = state.getValue(OPEN);
        boolean mirrored = state.getValue(MIRRORED);
        BlockPos bePos = upper ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(bePos);
        if (be instanceof MetalLockableDoorBlockEntity door) {
            if (door.surpassedHalfDoorAnim()) return open ?
                    getShape(mirrored ? facing.getClockWise() : facing.getCounterClockWise()) : getShape(facing);
            else return open ? getShape(facing) : getShape(mirrored ? facing.getClockWise() : facing.getCounterClockWise());
        }
        return open ? getShape(mirrored ? facing.getClockWise() : facing.getCounterClockWise()) : getShape(facing);
    }

    private static VoxelShape getShape(Direction facing) {
        return switch (facing) {
            case SOUTH -> SOUTH_AABB;
            case NORTH -> NORTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            default -> Shapes.block();
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(UPPER_HALF, true), 3);
    }

    private static final MapCodec<MetalLockableDoorBlock> CODEC = simpleCodec(MetalLockableDoorBlock::new);
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(UPPER_HALF) ? null : new MetalLockableDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.METAL_LOCKABLE_DOOR.get() ?
                (l, p, s, be) -> ((ITickable)be).update() : null;
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
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (!state.getValue(OPEN) && !state.getValue(LOCKED) && hitResult.getDirection() == state.getValue(FACING)) {
                boolean upper = state.getValue(UPPER_HALF);
                level.setBlock(pos, state.setValue(LOCKED, true), 10);
                BlockPos otherPos = upper ? pos.below() : pos.above();
                BlockState otherState = level.getBlockState(otherPos);
                if (otherState.is(this))
                    level.setBlock(otherPos, otherState.setValue(LOCKED, true), 10);
                playDoorSoundVariable(level, pos, state, BMNWSounds.LOCK.get());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return this.setOpen(level, pos, state, hitResult.getDirection(), !state.getValue(OPEN)) ?
                InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    public boolean setOpen(Level level, BlockPos pos, BlockState state, Direction clicked, boolean open) {
        if (state.getValue(OPEN) == open) return false;
        boolean upper = state.getValue(UPPER_HALF);
        boolean locked = state.getValue(LOCKED);
        if (locked && clicked != state.getValue(FACING)) {
            playDoorSoundVariable(level, pos, state, BMNWSounds.LOCK.get());
            return true;
        }
        BlockPos lowerPos = upper ? pos.below() : pos;
        BlockEntity be = level.getBlockEntity(lowerPos);
        if (be instanceof MetalLockableDoorBlockEntity door && !door.canSwitchState()) return false;
        if (level.isClientSide) return true;

        state = state.setValue(OPEN, open).setValue(LOCKED, false);
        level.setBlock(pos, state, 10);
        if (open) {
            playDoorSound(level, pos, state, BMNWSounds.METAL_LOCKABLE_DOOR_OPEN.get());
        } else {
            playDoorSound(level, pos, state, BMNWSounds.METAL_LOCKABLE_DOOR_CLOSE.get());
        }

        BlockPos otherPos = upper ? pos.below() : pos.above();
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.is(this)) {
            level.setBlock(otherPos, otherState.setValue(OPEN, open).setValue(LOCKED, false), 10);
        }

        if (be instanceof MetalLockableDoorBlockEntity door) door.setOpen(open);

        return true;
    }

    public static void playDoorSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        boolean upper = state.getValue(UPPER_HALF);
        double x = pos.getX() + 0.5;
        double y = upper ? pos.getY() : pos.getY() + 1;
        double z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS);
    }
    public static void playDoorSoundVariable(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
        boolean upper = state.getValue(UPPER_HALF);
        double x = pos.getX() + 0.5;
        double y = upper ? pos.getY() : pos.getY() + 1;
        double z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("block.bmnw.metal_lock_door.desc").withColor(0x888888));
    }
}
