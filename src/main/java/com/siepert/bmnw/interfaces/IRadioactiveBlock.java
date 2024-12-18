package com.siepert.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRadioactiveBlock {
    default float getRadioactivity(Level level, BlockPos pos, BlockState state) {
        return getRadioactivity();
    }
    float getRadioactivity();
}
