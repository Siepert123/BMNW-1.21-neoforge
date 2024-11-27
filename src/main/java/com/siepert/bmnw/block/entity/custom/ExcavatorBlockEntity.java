package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExcavatorBlockEntity extends BlockEntity {
    public ExcavatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.EXCAVATOR.get(), pos, blockState);
    }

    private void tick() {

    }

    public static void tick(ExcavatorBlockEntity be) {
        be.tick();
    }
}
