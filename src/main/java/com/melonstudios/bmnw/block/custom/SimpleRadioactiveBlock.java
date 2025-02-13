package com.melonstudios.bmnw.block.custom;

import com.melonstudios.bmnw.hazard.HazardRegistry;
import com.melonstudios.bmnw.radiation.RadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import com.melonstudios.bmnw.item.custom.SimpleRadioactiveBlockItem;
import com.melonstudios.bmnw.item.custom.SimpleRadioactiveItem;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple radioactive block implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlock extends Block {
    public SimpleRadioactiveBlock(Properties properties, float rads) {
        super(properties);
        HazardRegistry.addRadRegistry(this, rads);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) RadiationManager.getInstance().putSource(level.dimension().location(), pos, HazardRegistry.getRadRegistry(this));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide()) RadiationManager.getInstance().removeSource(level.dimension().location(), pos);
    }
}
