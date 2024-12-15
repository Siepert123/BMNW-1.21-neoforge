package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.custom.HatchBlock;
import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HatchBlockEntity extends BlockEntity {
    public HatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.HATCH.get(), pos, blockState);
        open = blockState.getValue(HatchBlock.OPEN);
    }

    public boolean open = false;
    public int ticks = 0;

    private void tick() {
        if (ticks > 0) ticks--;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        ((HatchBlockEntity)be).tick();
    }
}
