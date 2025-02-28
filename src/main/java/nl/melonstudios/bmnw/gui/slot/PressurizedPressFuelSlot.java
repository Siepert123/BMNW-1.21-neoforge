package nl.melonstudios.bmnw.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import nl.melonstudios.bmnw.gui.menu.PressurizedPressMenu;

public class PressurizedPressFuelSlot extends Slot {
    private final PressurizedPressMenu menu;

    public PressurizedPressFuelSlot(PressurizedPressMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.menu.isFuel(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.is(Items.BUCKET) ? 1 : super.getMaxStackSize(stack);
    }
}
