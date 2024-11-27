package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.misc.ExcavationVein;
import net.minecraft.world.item.Item;

public class CoreSampleItem extends Item {
    private final ExcavationVein vein;
    public CoreSampleItem(Properties properties, ExcavationVein associatedVein) {
        super(properties.stacksTo(1));
        this.vein = associatedVein;
    }

    public ExcavationVein getVein() {
        return this.vein;
    }
}
