package nl.melonstudios.bmnw.interfaces;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IExtendedEnergyStorage extends IEnergyStorage {
    void setEnergyStored(int amount);
}
