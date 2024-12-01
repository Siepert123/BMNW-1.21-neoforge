package com.siepert.bmnw.interfaces;

import net.minecraft.world.item.ItemStack;

public interface IBatteryItem {
    int getMaxStoredEnergy();
    int getStoredEnergy(ItemStack stack);
    int getMaxEnergyTransfer();

    void setStoredEnergy(ItemStack stack, int amount);
    void addStoredEnergy(ItemStack stack, int amount);
    void removeStoredEnergy(ItemStack stack, int amount);

    int drainStoredEnergy(ItemStack stack);

    default int getRemainingEnergyStorageSpace(ItemStack stack) {
        return getMaxStoredEnergy() - getStoredEnergy(stack);
    }
    default int tryInsertEnergy(ItemStack stack, int amount) {
        return Math.min(getRemainingEnergyStorageSpace(stack), amount);
    }
    default int tryRemoveEnergy(ItemStack stack, int amount) {
        return Math.min(getStoredEnergy(stack), amount);
    }

    void requireNonNullComponent(ItemStack stack);
}
