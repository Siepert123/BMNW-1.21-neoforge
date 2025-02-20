package nl.melonstudios.bmnw.item.misc;

import net.minecraft.world.level.block.Block;
import nl.melonstudios.bmnw.hazard.HazardRegistry;

public class NuclearRemainsBlockItem extends SimpleRadioactiveBlockItem {
    public <T extends Block> NuclearRemainsBlockItem(T block, Properties properties) {
        super(block, properties);
        HazardRegistry.addBurningRegistry(this, true);
    }
}
