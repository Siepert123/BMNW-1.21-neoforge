package com.melonstudios.bmnw.item.custom;

import com.melonstudios.bmnw.misc.ExcavationVein;
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
