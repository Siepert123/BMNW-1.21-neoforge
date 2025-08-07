package nl.melonstudios.bmnw.logistics.cables;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;
import nl.melonstudios.bmnw.interfaces.IPrioritizedEnergyStorage;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class CableNetEnergyStorage implements IEnergyStorage {
    private static final ThreadLocal<Set<Object>> MUTABLE_SET = ThreadLocal.withInitial(ObjectArraySet::new);
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
        for (EnergyStorageLocation location : sortedLocations) {
            IEnergyStorage storage = location.getEnergyStorageAt(level);
            if (storage != null && storage.canReceive() && storage.receiveEnergy(toReceive, true) > 0) candidates.add(storage);
        }
        {
            Set<IEnergyStorage> set = new ObjectArraySet<>();
            set.addAll(candidates);
            candidates.clear();
            candidates.addAll(set);
            set.clear();
        }
        candidates.sort((l, r) -> {
            if (l == r) return 0;
            int lPriority = IPrioritizedEnergyStorage.getPriority(l);
            int rPriority = IPrioritizedEnergyStorage.getPriority(r);
            int priorityComparison = Integer.compare(rPriority, lPriority);
            if (priorityComparison != 0) return priorityComparison;
            random.setSeed(seed ^ (System.identityHashCode(lPriority) | Integer.toUnsignedLong(System.identityHashCode(rPriority)) << 32));
            return random.nextBoolean() ? -1 : 1;
        });

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
        if (toExtract <= 0) return 0;

        ServerLevel level = this.cableNet.level;
        List<IEnergyStorage> candidates = MUTABLE_STORAGE_LIST.get();
        candidates.clear();
        List<EnergyStorageLocation> sortedLocations = MUTABLE_LOCATION_LIST.get();
        sortedLocations.clear();
        sortedLocations.addAll(this.cableNet.energyStorageLocations);
        Random random = MUTABLE_RANDOM.get();
        long seed = System.nanoTime();
        for (EnergyStorageLocation location : sortedLocations) {
            IEnergyStorage storage = location.getEnergyStorageAt(level);
            if (storage != null && storage.canExtract() && storage.extractEnergy(toExtract, true) > 0)
                candidates.add(storage);
        }
        {
            Set<IEnergyStorage> set = new ObjectArraySet<>();
            set.addAll(candidates);
            candidates.clear();
            candidates.addAll(set);
            set.clear();
        }
        candidates.sort((l, r) -> {
            if (l == r) return 0;
            int lPriority = IPrioritizedEnergyStorage.getPriority(l);
            int rPriority = IPrioritizedEnergyStorage.getPriority(r);
            int priorityComparison = Integer.compare(rPriority, lPriority);
            if (priorityComparison != 0) return priorityComparison;
            random.setSeed(seed ^ (System.identityHashCode(lPriority) | Integer.toUnsignedLong(System.identityHashCode(rPriority)) << 32));
            return random.nextBoolean() ? -1 : 1;
        });

        int remaining = toExtract;
        int drained = 0;

        for (IEnergyStorage storage : candidates) {
            if (remaining <= 0) break;
            int amount = storage.extractEnergy(remaining, simulate);
            remaining -= amount;
            drained += amount;
        }

        return drained;
    }

    @Override
    public int getEnergyStored() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
