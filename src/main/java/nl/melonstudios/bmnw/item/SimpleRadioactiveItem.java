package nl.melonstudios.bmnw.item;

import net.minecraft.world.item.Item;
import nl.melonstudios.bmnw.block.SimpleRadioactiveBlock;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.interfaces.IItemHazard;

/**
 * Simple radioactive item implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveBlock
 */
public class SimpleRadioactiveItem extends Item implements IItemHazard {
    private final float rads;
    public SimpleRadioactiveItem(Properties properties, float rads) {
        super(properties);
        this.rads = rads;
        HazardRegistry.addRadRegistry(this, rads);
    }

    public SimpleRadioactiveItem(float rads) {
        this(new Properties(), rads);
    }

    @Override
    public float getRadioactivity() {
        return rads;
    }
}
