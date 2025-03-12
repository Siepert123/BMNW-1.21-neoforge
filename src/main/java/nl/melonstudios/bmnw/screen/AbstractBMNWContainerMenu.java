package nl.melonstudios.bmnw.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;

public abstract class AbstractBMNWContainerMenu extends AbstractContainerMenu {
    protected AbstractBMNWContainerMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected void addPlayerInventory(Inventory playerInventory, int xOffset, int yOffset) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j+i*9+9, xOffset+j*18, yOffset+i*18));
            }
        }
    }
    protected void addPlayerHotbar(Inventory playerInventory, int xOffset, int yOffset) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, xOffset+i*18, yOffset));
        }
    }
}
