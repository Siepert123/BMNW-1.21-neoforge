package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.interfaces.IRadioactiveBlock;
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
    private final long femtoRads;
    public SimpleRadioactiveBlock(Properties properties, long femtoRads) {
        super(properties);
        this.femtoRads = femtoRads;
    }

    @Override
    public long radioactivity() {
        return femtoRads;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        RadHelper.recalculateChunkRadioactivity(level.getChunk(pos));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        RadHelper.recalculateChunkRadioactivity(level.getChunk(pos));
    }
}
