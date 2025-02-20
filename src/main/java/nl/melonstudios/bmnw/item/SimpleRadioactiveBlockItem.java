package nl.melonstudios.bmnw.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import nl.melonstudios.bmnw.block.SimpleRadioactiveBlock;
import nl.melonstudios.bmnw.hazard.HazardRegistry;

/**
 * Simple radioactive block item implementation.
 * @see SimpleRadioactiveBlock
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlockItem extends BlockItem {
    private final float rads;
    public <T extends Block> SimpleRadioactiveBlockItem(T block, Properties properties) {
        super(block, properties);
        this.rads = HazardRegistry.getRadRegistry(block);
        HazardRegistry.addRadRegistry(this, this.rads);
    }
    public SimpleRadioactiveBlockItem(Block block, Properties properties, float rads) {
        super(block, properties);
        this.rads = rads;
        HazardRegistry.addRadRegistry(this, rads);
    }
}
