package nl.melonstudios.bmnw.screen.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.init.BMNWTags;

public class FuelSlot extends SlotItemHandler {
    public FuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getBurnTime(null) > 0 || stack.is(BMNWTags.Items.INFINITE_FUEL_SOURCES);
    }
}
