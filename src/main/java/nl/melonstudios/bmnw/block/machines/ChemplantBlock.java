package nl.melonstudios.bmnw.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import nl.melonstudios.bmnw.block.entity.ChemplantBlockEntity;
import nl.melonstudios.bmnw.block.entity.DummyBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.interfaces.IHeatable;
import org.jetbrains.annotations.Nullable;

public class ChemplantBlock extends Block implements EntityBlock, IHeatable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ChemplantBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(pos, x, y, z);
                    if (pos.asLong() == mutable.asLong()) continue;
                    level.setBlock(mutable, BMNWBlocks.DUMMY.get().defaultBlockState(), 3);
                    BlockEntity be = level.getBlockEntity(mutable);
                    if (be instanceof DummyBlockEntity dummy) {
                        dummy.setCore(pos);
                    }
                }
            }
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChemplantBlockEntity plant) {
            plant.initialized = true;
            plant.notifyChange();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(pos, x, y, z);
                    if (pos.asLong() == mutable.asLong()) continue;
                    if (level.getBlockState(mutable).is(BMNWBlocks.DUMMY.get())) level.destroyBlock(mutable, false);
                }
            }
        }
        level.invalidateCapabilities(pos);
        level.removeBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChemplantBlockEntity(pos, state);
    }

    @Override
    public boolean canAcceptHeat(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public int acceptHeat(Level level, BlockPos pos, BlockState state, int amount) {
        return 0;
    }
}
