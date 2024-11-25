package com.siepert.bmnw.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Plutonium238Block extends SimpleRadioactiveBlock {
    public Plutonium238Block(Properties properties, float rads) {
        super(properties, rads);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setRemainingFireTicks(10);
    }
}
