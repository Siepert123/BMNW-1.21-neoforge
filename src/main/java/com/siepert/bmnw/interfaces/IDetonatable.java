package com.siepert.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IDetonatable {
    void detonate(Level level, BlockPos pos);
}
