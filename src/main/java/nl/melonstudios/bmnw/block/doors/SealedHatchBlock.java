package nl.melonstudios.bmnw.block.doors;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.SealedHatchBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SealedHatchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 3, 16);

    public static final VoxelShape SHAPE_OPEN = Shapes.or(
            box(0, 0, 0, 1, 1, 16),
            box(15, 0, 0, 16, 1, 16),
            box(0, 0, 0, 16, 1, 1),
            box(0, 0, 15, 16, 1, 16)
    );

    public SealedHatchBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context.isDescending()) return SHAPE;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SealedHatchBlockEntity hatch)
            return hatch.mayPass() ? SHAPE_OPEN : SHAPE;
        return state.getValue(OPEN) ? SHAPE_OPEN : SHAPE;
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SealedHatchBlockEntity hatch)
            return hatch.mayPass() ? Shapes.empty() : this.getShape(state, level, pos, context);
        return state.getValue(OPEN) ? Shapes.empty() : this.getShape(state, level, pos, context);
    }

    private static final MapCodec<SealedHatchBlock> CODEC = simpleCodec(SealedHatchBlock::new);
    @Override
    protected MapCodec<SealedHatchBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SealedHatchBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.setOpen(level, pos, state, !state.getValue(OPEN)) ?
                InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.SEALED_HATCH.get() ? (l, p, s, be) -> ((ITickable)be).update() : null;
    }

    public boolean setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        if (state.getValue(OPEN) == open) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SealedHatchBlockEntity hatch && !hatch.canSwitchState()) return false;
        if (level.isClientSide) return true;

        state = state.setValue(OPEN, open);
        level.setBlock(pos, state, 10);
        if (open) {
            level.playSound(null, pos, BMNWSounds.HATCH_OPEN_FULL.get(), SoundSource.BLOCKS);
        } else {
            level.playSound(null, pos, BMNWSounds.HATCH_CLOSE_FULL.get(), SoundSource.BLOCKS);
        }

        if (be instanceof SealedHatchBlockEntity hatch) {
            hatch.setOpen(open);
        }
        return true;
    }
}
