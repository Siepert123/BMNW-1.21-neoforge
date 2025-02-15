package nl.melonstudios.bmnw.hardcoded.lootpool;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.function.Supplier;

public class StateSupplierPoolEntry {
    private final RandomStateSupplier supplier;
    private final int weight;

    public StateSupplierPoolEntry(RandomStateSupplier supplier) {
        this(supplier, 1);
    }
    public StateSupplierPoolEntry(RandomStateSupplier supplier, int weight) {
        if (weight < 1) throw new IllegalArgumentException("Loot pool entry weight must be above 0!");
        this.weight = weight;
        this.supplier = supplier;
    }

    public int getWeight() {
        return this.weight;
    }

    public BlockState getState(Random random) {
        return this.supplier.supply(random);
    }
}
