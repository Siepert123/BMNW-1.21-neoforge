package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import com.siepert.bmnw.block.custom.SimpleRadioactiveBlock;

import java.util.List;

/**
 * Simple radioactive block item implementation.
 * @see SimpleRadioactiveBlock
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlockItem extends BlockItem implements IItemHazard {
    private final float rads;
    public <T extends Block & IRadioactiveBlock> SimpleRadioactiveBlockItem(T block, Properties properties) {
        super(block, properties);
        this.rads = block.getRadioactivity();
    }
    public SimpleRadioactiveBlockItem(Block block, Properties properties, float rads) {
        super(block, properties);
        this.rads = rads;
    }

    @Override
    public float getRadioactivity() {
        return rads;
    }
}
