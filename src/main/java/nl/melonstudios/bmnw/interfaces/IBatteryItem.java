package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.item.components.BMNWDataComponents;

/**
 * Interface for all items containing RF (or FE or Flux or whatever you wish to call it)
 */
public interface IBatteryItem {
    /**
     * @return The energy capacity of the battery.
     */
    int getMaxStoredEnergy();

    /**
     * @param stack The itemstack to obtain the RF from
     * @return The amount of RF stored in the battery
     */
    int getStoredEnergy(ItemStack stack);

    /**
     * @return How much RF is allowed to be transferred per tick
     */
    int getMaxEnergyTransfer();

    /**
     * Directly modifies the amount of RF in a battery.
     * @param stack The itemstack to modify
     * @param amount How much RF the battery should have
     */
    void setStoredEnergy(ItemStack stack, int amount);

    /**
     * Adds RF to the battery.
     * @param stack The itemstack to modify
     * @param amount How much you wish to add to the battery.
     */
    void addStoredEnergy(ItemStack stack, int amount);

    /**
     * Removes RF from the battery.
     * @param stack The itemstack to modify
     * @param amount How much you wish to remove from the battery.
     */
    void removeStoredEnergy(ItemStack stack, int amount);

    /**
     * Removes ALL RF from the battery.
     * @param stack The itemstack to modify
     * @return How much RF was in the battery before drain.
     */
    int drainStoredEnergy(ItemStack stack);

    /**
     * How much space for RF there is left.
     * Inverse of {@code getStoredEnergy()}.
     * @param stack The battery to check the energy space.
     * @return How much more RF can be added before the capacity is reached.
     */
    default int getRemainingEnergyStorageSpace(ItemStack stack) {
        return getMaxStoredEnergy() - getStoredEnergy(stack);
    }

    /**
     * @param stack The battery to check.
     * @param amount How much you wish to insert.
     * @return How much would actually be inserted.
     */
    default int tryInsertEnergy(ItemStack stack, int amount) {
        return Math.min(getRemainingEnergyStorageSpace(stack), amount);
    }

    /**
     * @param stack The battery to check.
     * @param amount How much you wish to extract.
     * @return How much would actually be extracted.
     */
    default int tryRemoveEnergy(ItemStack stack, int amount) {
        return Math.min(getStoredEnergy(stack), amount);
    }

    /**
     * Denullifies the data component meant for energy storage.
     * Recommended data component is {@link BMNWDataComponents#STORED_BATTERY_RF}.
     * @param stack Stack to denullify the battery data component of.
     */
    void requireNonNullComponent(ItemStack stack);
}
