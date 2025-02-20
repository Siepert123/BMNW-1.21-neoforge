package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.interfaces.IBatteryItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InfiniteBatteryItem extends Item implements IBatteryItem {
    public InfiniteBatteryItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("∞RF").withColor(0xaaaaaa));
    }

    @Override
    public int getMaxStoredEnergy() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getStoredEnergy(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnergyTransfer() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setStoredEnergy(ItemStack stack, int amount) {

    }

    @Override
    public void addStoredEnergy(ItemStack stack, int amount) {

    }

    @Override
    public void removeStoredEnergy(ItemStack stack, int amount) {

    }

    @Override
    public int drainStoredEnergy(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void requireNonNullComponent(ItemStack stack) {

    }
}
