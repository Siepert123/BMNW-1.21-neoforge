package nl.melonstudios.bmnw.hardcoded.lootpool.coded;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPool;
import nl.melonstudios.bmnw.hardcoded.lootpool.StateSupplierPoolEntry;

import java.util.Random;

public class LootPoolStateSupplier extends LootPool<BlockState> {
    private final StateSupplierPoolEntry[] entries;
    private final int totalWeight;

    public LootPoolStateSupplier(StateSupplierPoolEntry... entries) {
        if (entries.length == 0) throw new IllegalArgumentException("Cannot have an empty loot pool!");
        this.entries = entries;
        int w = 0;
        for (StateSupplierPoolEntry entry : entries) {
            w += entry.getWeight();
        }
        this.totalWeight = w;
    }

    @Override
    public BlockState get(Random random) {
        int i = random.nextInt(this.totalWeight);
        int c = 0;
        for (StateSupplierPoolEntry entry : this.entries) {
            c += entry.getWeight();
            if (i < c) return entry.getState(random);
        }
        return Blocks.AIR.defaultBlockState();
    }
}
