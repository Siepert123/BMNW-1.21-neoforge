package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExcavatorBlockEntitySlave extends BlockEntity {
    public ExcavatorBlockEntitySlave(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.EXCAVATOR_SLAVE.get(), pos, blockState);
    }
}
