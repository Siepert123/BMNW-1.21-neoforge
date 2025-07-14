package nl.melonstudios.bmnw.interfaces;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IPrioritizedEnergyStorage extends IEnergyStorage {
    int getPriority();

    static int getPriority(IEnergyStorage storage) {
        return storage instanceof IPrioritizedEnergyStorage prioritized ? prioritized.getPriority() : 0;
    }
}
