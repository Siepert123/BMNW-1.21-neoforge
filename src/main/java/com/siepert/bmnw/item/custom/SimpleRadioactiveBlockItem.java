package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.siepert.bmnw.block.custom.SimpleRadioactiveBlock;

/**
 * Simple radioactive block item implementation.
 * @see SimpleRadioactiveBlock
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlockItem extends BlockItem {
    private final float rads;
    public <T extends Block> SimpleRadioactiveBlockItem(T block, Properties properties) {
        super(block, properties);
        this.rads = HazardRegistry.getRadRegistry(block);
        HazardRegistry.addRadRegistry(this, this.rads);
    }
    public SimpleRadioactiveBlockItem(Block block, Properties properties, float rads) {
        super(block, properties);
        this.rads = rads;
        HazardRegistry.addRadRegistry(this, rads);
    }
}
