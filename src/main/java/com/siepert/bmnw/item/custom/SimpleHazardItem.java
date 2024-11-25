package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import net.minecraft.world.item.Item;

public class SimpleHazardItem extends Item implements IItemHazard {
    private final float rads;
    private final boolean burning;
    private final boolean blinding;
    public SimpleHazardItem(Properties properties, float rads, boolean burning, boolean blinding) {
        super(properties);
        this.rads = rads;
        this.burning = burning;
        this.blinding = blinding;
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

    @Override
    public float getRadioactivity() {
        return rads;
    }

    @Override
    public boolean isBurning() {
        return burning;
    }

    @Override
    public boolean isBlinding() {
        return blinding;
    }
}
