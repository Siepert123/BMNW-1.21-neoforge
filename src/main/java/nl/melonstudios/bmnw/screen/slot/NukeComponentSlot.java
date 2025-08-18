package nl.melonstudios.bmnw.screen.slot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class NukeComponentSlot extends SlotItemHandler {
    private final Item requiredItem;

    public NukeComponentSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item requiredItem) {
        super(itemHandler, index, xPosition, yPosition);

        this.requiredItem = requiredItem;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(this.requiredItem);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
