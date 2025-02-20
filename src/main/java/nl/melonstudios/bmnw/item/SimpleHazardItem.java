package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.hazard.HazardRegistry;
import net.minecraft.world.item.Item;

@Deprecated(since = "0.2.0")
public class SimpleHazardItem extends Item {
    public SimpleHazardItem(Properties properties, float rads, boolean burning, boolean blinding) {
        super(properties);
        HazardRegistry.addRadRegistry(this, rads);
        HazardRegistry.addBurningRegistry(this, burning);
        HazardRegistry.addBlindingRegistry(this, blinding);
    }
    public SimpleHazardItem(float rads, boolean burning, boolean blinding) {
        this(new Properties(), rads, burning, blinding);
    }
    public SimpleHazardItem(Properties properties, float rads, boolean burning) {
        this(properties, rads, burning, false);
    }
    public SimpleHazardItem(float rads, boolean burning) {
        this(new Properties(), rads, burning, false);
    }
}
