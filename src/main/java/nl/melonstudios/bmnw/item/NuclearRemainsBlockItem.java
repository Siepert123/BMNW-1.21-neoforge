package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.hazard.HazardRegistry;
import net.minecraft.world.level.block.Block;

public class NuclearRemainsBlockItem extends SimpleRadioactiveBlockItem {
    public <T extends Block> NuclearRemainsBlockItem(T block, Properties properties) {
        super(block, properties);
        HazardRegistry.addBurningRegistry(this, true);
    }
}
