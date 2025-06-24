package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public interface IInfiniteFluidSupply {
    boolean compatible(ItemStack stack, Fluid fluid);
    int transferSpeed(ItemStack stack);
}
