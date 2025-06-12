package nl.melonstudios.bmnw.misc;

import net.neoforged.neoforge.energy.EnergyStorage;
import nl.melonstudios.bmnw.interfaces.IExtendedEnergyStorage;

public class ExtendedEnergyStorage extends EnergyStorage implements IExtendedEnergyStorage {
    public ExtendedEnergyStorage(int capacity) {
        super(capacity);
    }

    public ExtendedEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public ExtendedEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public ExtendedEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public void setEnergyStored(int amount) {
        this.energy = amount;
    }
}
