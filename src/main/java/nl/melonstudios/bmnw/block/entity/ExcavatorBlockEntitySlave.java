package nl.melonstudios.bmnw.block.entity;

import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExcavatorBlockEntitySlave extends BlockEntity {
    public ExcavatorBlockEntitySlave(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.EXCAVATOR_SLAVE.get(), pos, blockState);
    }

    private BlockPos corePos = BlockPos.ZERO;
    public void setCore(BlockPos pos) {
        this.corePos = pos;
    }
    public BlockPos getCore() {
        return this.corePos;
    }
}
