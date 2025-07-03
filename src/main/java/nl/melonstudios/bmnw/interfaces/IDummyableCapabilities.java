package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * Interface for BlockEntities who have dummy blocks and also have capabilities.
 */
public interface IDummyableCapabilities {
    default @Nullable IItemHandler getItemHandler(Vec3i offset, @Nullable Direction face) {
        return null;
    }
    default @Nullable IFluidHandler getFluidHandler(Vec3i offset, @Nullable Direction face) {
        return null;
    }
    default @Nullable IEnergyStorage getEnergyStorage(Vec3i offset, @Nullable Direction face) {
        return null;
    }
}
