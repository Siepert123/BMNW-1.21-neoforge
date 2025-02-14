package nl.melonstudios.bmnw.item.custom;

import nl.melonstudios.bmnw.block.BMNWBlocks;
import nl.melonstudios.bmnw.hazard.HazardRegistry;

public class Plutonium238BlockItem extends SimpleRadioactiveBlockItem {
    public Plutonium238BlockItem() {
        super(BMNWBlocks.PLUTONIUM_238_BLOCK.get(), new Properties());
        HazardRegistry.addBurningRegistry(this, true);
    }
}
