package nl.melonstudios.bmnw.screen.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.hardcoded.recipe.StampType;

public class MoldSlot extends SlotItemHandler {
    public MoldSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return StampType.getMoldType(stack) != null;
    }
}
