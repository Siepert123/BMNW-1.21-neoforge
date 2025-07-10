package nl.melonstudios.bmnw.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import nl.melonstudios.bmnw.block.entity.ExcavatorBlockEntity;
import nl.melonstudios.bmnw.block.entity.ExcavatorBlockEntitySlave;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import org.jetbrains.annotations.Nullable;

public class ExcavatorBlock extends Block implements EntityBlock {
    public static final BooleanProperty MULTIBLOCK_SLAVE = BMNWStateProperties.MULTIBLOCK_SLAVE;
    public ExcavatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MULTIBLOCK_SLAVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(MULTIBLOCK_SLAVE) ? new ExcavatorBlockEntitySlave(pos, state) : new ExcavatorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return state.getValue(MULTIBLOCK_SLAVE) ? null :
                (blockEntityType == BMNWBlockEntities.EXCAVATOR.get() ? (BlockEntityTicker<T>) (level1, pos, state1, be) -> ExcavatorBlockEntity.tick((ExcavatorBlockEntity) be) : null);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        for (int i = -1; i <= 1; i++) {
            for (int j = 0; j <= 2; j++) {
                for (int k = 0; k <= 2; k++) {
                    BlockPos otherPos = pos.offset(i, j, k);
                    if (level.getBlockState(otherPos).canBeReplaced())
                        level.setBlock(otherPos, this.defaultBlockState().setValue(MULTIBLOCK_SLAVE, true), 3);
                }
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
