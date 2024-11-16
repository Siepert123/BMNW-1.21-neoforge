package com.siepert.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRadioactiveBlock {
    long radioactivity(Level level, BlockPos pos, BlockState state);
}
