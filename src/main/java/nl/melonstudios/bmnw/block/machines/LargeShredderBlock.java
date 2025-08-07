package nl.melonstudios.bmnw.block.machines;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.decoration.CatwalkRailingBlock;
import nl.melonstudios.bmnw.block.entity.LargeShredderBlockEntity;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.BrokenConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeShredderBlock extends HorizontalDirectionalBlock implements EntityBlock, CatwalkRailingBlock.ISupportsCatwalkRailing {
    public static final BooleanProperty MULTIBLOCK_SLAVE = BMNWStateProperties.MULTIBLOCK_SLAVE;
    public LargeShredderBlock(Properties properties) {
        super(properties.noOcclusion());

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(MULTIBLOCK_SLAVE, false)
        );
    }

    public static final VoxelShape HOLE_SHAPE = Shapes.or(
            box(2, 0, 2, 14, 6, 14),
            box(0, 0, 0, 2, 16, 16),
            box(14, 0, 0, 16, 16, 16),
            box(0, 0, 0, 16, 16, 2),
            box(0, 0, 14, 16, 16, 16)
    );

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(MULTIBLOCK_SLAVE)) {
            BlockState below = level.getBlockState(pos.below());
            if (below.is(this) && !below.getValue(MULTIBLOCK_SLAVE)) return HOLE_SHAPE;
        }
        return Shapes.block();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(MULTIBLOCK_SLAVE);
    }

    public static final MapCodec<LargeShredderBlock> CODEC = simpleCodec(LargeShredderBlock::new);
    @Override
    protected MapCodec<? extends LargeShredderBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(MULTIBLOCK_SLAVE) ? null : new LargeShredderBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (l, p, s, be) -> ((ITickable)be).update();
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.getValue(MULTIBLOCK_SLAVE)) return;
        forSlave(pos, state, (pos1) -> {
            level.setBlock(pos1, BMNWBlocks.LARGE_SHREDDER.get().defaultBlockState().setValue(MULTIBLOCK_SLAVE, true), 3);
            return Boolean.FALSE;
        });
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(MULTIBLOCK_SLAVE) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getValue(MULTIBLOCK_SLAVE)) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutable.setWithOffset(pos, x, y, z);
                        BlockState otherState = level.getBlockState(mutable);
                        if (!otherState.is(this) || otherState.getValue(BMNWStateProperties.MULTIBLOCK_SLAVE)) continue;
                        BlockEntity other = level.getBlockEntity(mutable);
                        if (other instanceof LargeShredderBlockEntity shredder) {
                            shredder.check();
                        }
                    }
                }
            }
        } else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LargeShredderBlockEntity shredder) {
                List<ItemStack> stacks = shredder.drops();
                for (ItemStack stack : stacks) {
                    if (stack.isEmpty()) continue;
                    ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
                    level.addFreshEntity(entity);
                }
            }
            forSlave(pos, state, (pos1) -> {
                level.destroyBlock(pos1, false);
                return false;
            });
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static void forSlave(BlockPos pos, BlockState state, BrokenConsumer<BlockPos> action) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Direction facing = state.getValue(FACING);

        mutable.setWithOffset(pos, Direction.UP);
        if (action.accept(mutable)) return;
        mutable.setWithOffset(pos, Direction.DOWN);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos, facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos, facing.getOpposite());
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.above(), facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.below(), facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.above(), facing.getOpposite());
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.below(), facing.getOpposite());
        action.accept(mutable);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
