package nl.melonstudios.bmnw.screen.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.interfaces.IBatteryItem;

import javax.annotation.Nullable;

public class BatterySlot extends SlotItemHandler {
    private final Boolean batteryType;
    public BatterySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, @Nullable Boolean batteryType) {
        super(itemHandler, index, xPosition, yPosition);
        this.batteryType = batteryType;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof IBatteryItem battery && (this.batteryType == null || battery.isLarge() == this.batteryType);
    }
}
