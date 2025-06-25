package nl.melonstudios.bmnw.item.tools;

import net.minecraft.world.item.Item;

@Deprecated(forRemoval = true)
public class EmptyFluidContainerItem extends Item {
    private final Item filled;
    public EmptyFluidContainerItem(Properties properties, Item filled) {
        super(properties);
        this.filled = filled;
    }

    public Item asFilled() {
        return this.filled;
    }
}
