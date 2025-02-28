package nl.melonstudios.bmnw.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.gui.slot.PressurizedPressFuelSlot;
import nl.melonstudios.bmnw.gui.slot.PressurizedPressResultSlot;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PressurizedPressMenu extends AbstractContainerMenu {
    public static final int FUEL_SLOT = 0;
    public static final int MOLD_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;
    public static final int SLOT_COUNT = 4;
    public static final int DATA_COUNT = 2;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final Container container;
    private final ContainerData data;
    protected final Level level;

    public PressurizedPressMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(BMNWMenuTypes.PRESSURIZED_PRESS.get(), containerId);

        this.container = container;
        this.data = containerData;
        this.level = playerInventory.player.level();

        this.addSlot(new PressurizedPressFuelSlot(this, container, 0, 16, 17));
        this.addSlot(new Slot(container, 1, 56, 17));
        this.addSlot(new Slot(container, 2, 56, 57));
        this.addSlot(new PressurizedPressResultSlot(playerInventory.player, container, 3, 106, 57));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(this.data);
    }
    public PressurizedPressMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4), new SimpleContainerData(2));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (this.canProcess(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, INPUT_SLOT, INPUT_SLOT+1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, FUEL_SLOT, FUEL_SLOT+1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(null) > 0;
    }

    @Override
    public MenuType<?> getType() {
        return BMNWMenuTypes.PRESSURIZED_PRESS.get();
    }

    public boolean canProcess(ItemStack stack) {
        ItemStack mold = this.container.getItem(MOLD_SLOT);
        if (PressingRecipes.MoldType.getMoldType(mold) != null) {
            ItemStack result = PressingRecipes.instance.getResult(mold, stack);
            return !result.isEmpty();
        }
        return false;
    }

    public float getPressProgress() {
        int i = this.data.get(1);
        if (i > 100) i = 100 - i;

        return Mth.clamp(i / 100f, 0, 1);
    }

    public boolean displayFlame() {
        ItemStack mold = this.container.getItem(MOLD_SLOT);
        if (PressingRecipes.MoldType.getMoldType(mold) != null) {
            ItemStack result = PressingRecipes.instance.getResult(mold, this.container.getItem(INPUT_SLOT));
            ItemStack s = this.container.getItem(OUTPUT_SLOT);
            return this.data.get(0) > 0 && !result.isEmpty() && (s.isEmpty() || ItemStack.isSameItemSameComponents(result, s));
        }
        return false;
    }
}
