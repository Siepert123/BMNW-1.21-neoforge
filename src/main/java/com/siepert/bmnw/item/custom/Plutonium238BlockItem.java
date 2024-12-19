package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.hazard.HazardRegistry;

public class Plutonium238BlockItem extends SimpleRadioactiveBlockItem {
    public Plutonium238BlockItem() {
        super(BMNWBlocks.PLUTONIUM_238_BLOCK.get(), new Properties());
        HazardRegistry.addBurningRegistry(this, true);
    }
}
