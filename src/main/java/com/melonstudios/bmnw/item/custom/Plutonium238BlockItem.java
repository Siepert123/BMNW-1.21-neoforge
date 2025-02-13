package com.melonstudios.bmnw.item.custom;

import com.melonstudios.bmnw.block.BMNWBlocks;
import com.melonstudios.bmnw.hazard.HazardRegistry;

public class Plutonium238BlockItem extends SimpleRadioactiveBlockItem {
    public Plutonium238BlockItem() {
        super(BMNWBlocks.PLUTONIUM_238_BLOCK.get(), new Properties());
        HazardRegistry.addBurningRegistry(this, true);
    }
}
