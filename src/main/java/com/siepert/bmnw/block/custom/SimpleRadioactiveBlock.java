package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.hazard.HazardRegistry;
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
public class SimpleRadioactiveBlock extends Block {
    public SimpleRadioactiveBlock(Properties properties, float rads) {
        super(properties);
        HazardRegistry.addRadRegistry(this, rads);
    }
}
