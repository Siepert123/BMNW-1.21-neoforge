package nl.melonstudios.bmnw.logistics.cables;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Random;

public class CableNetEnergyStorage implements IEnergyStorage {
    private static final ThreadLocal<List<IEnergyStorage>> MUTABLE_STORAGE_LIST = ThreadLocal.withInitial(ObjectArrayList::new);
    private static final ThreadLocal<List<EnergyStorageLocation>> MUTABLE_LOCATION_LIST = ThreadLocal.withInitial(ObjectArrayList::new);
    private static final ThreadLocal<Random> MUTABLE_RANDOM = ThreadLocal.withInitial(Random::new);

    private final CableNet cableNet;

    public CableNetEnergyStorage(CableNet cableNet) {
        this.cableNet = cableNet;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (toReceive <= 0) return 0;

        ServerLevel level = this.cableNet.level;
        List<IEnergyStorage> candidates = MUTABLE_STORAGE_LIST.get();
        candidates.clear();
        List<EnergyStorageLocation> sortedLocations = MUTABLE_LOCATION_LIST.get();
        sortedLocations.clear();
        sortedLocations.addAll(this.cableNet.energyStorageLocations);
        Random random = MUTABLE_RANDOM.get();
        long seed = System.nanoTime();
        sortedLocations.sort((l, r) -> {
            if (l.pos() == r.pos()) return 0;
            random.setSeed(seed ^ (l.pos().hashCode() | Integer.toUnsignedLong(r.pos().hashCode()) << 32));
            return random.nextBoolean() ? -1 : 1;
        });
        for (EnergyStorageLocation location : sortedLocations) {
            IEnergyStorage storage = location.getEnergyStorageAt(level);
            if (storage != null && storage.canReceive() && storage.receiveEnergy(toReceive, true) > 0) candidates.add(storage);
        }

        int remaining = toReceive;
        int filled = 0;

        for (IEnergyStorage storage : candidates) {
            if (remaining <= 0) break;
            int amount = storage.receiveEnergy(remaining, simulate);
            remaining -= amount;
            filled += amount;
        }

        return filled;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
