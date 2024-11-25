package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.block.BMNWBlocks;

public class Plutonium238BlockItem extends SimpleRadioactiveBlockItem {
    public Plutonium238BlockItem() {
        super(BMNWBlocks.PLUTONIUM_238_BLOCK.get(), new Properties());
    }

    @Override
    public boolean isBurning() {
        return true;
    }
}
