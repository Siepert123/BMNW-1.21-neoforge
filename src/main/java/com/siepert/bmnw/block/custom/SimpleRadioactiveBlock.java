package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.radiation.RadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.siepert.bmnw.item.custom.SimpleRadioactiveBlockItem;
import com.siepert.bmnw.item.custom.SimpleRadioactiveItem;

/**
 * Simple radioactive block implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlock extends Block implements IRadioactiveBlock {
    private final float rads;
    public SimpleRadioactiveBlock(Properties properties, float rads) {
        super(properties);
        this.rads = rads;
    }

    @Override
    public float getRadioactivity() {
        return rads;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (BMNWConfig.radiationOptimizer) {
            RadHelper.modifySourceRadioactivity(level.getChunk(pos), rads);
        } else {
            RadHelper.recalculateChunkRadioactivity(level.getChunk(pos));
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (BMNWConfig.radiationOptimizer) {
            RadHelper.modifySourceRadioactivity(level.getChunk(pos), -rads);
        } else {
            RadHelper.recalculateChunkRadioactivity(level.getChunk(pos));
        }
    }
}
