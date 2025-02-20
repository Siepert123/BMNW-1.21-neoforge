package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.hazard.HazardRegistry;

public class Plutonium238BlockItem extends SimpleRadioactiveBlockItem {
    public Plutonium238BlockItem() {
        super(BMNWBlocks.PLUTONIUM_238_BLOCK.get(), new Properties());
        HazardRegistry.addBurningRegistry(this, true);
    }
}
