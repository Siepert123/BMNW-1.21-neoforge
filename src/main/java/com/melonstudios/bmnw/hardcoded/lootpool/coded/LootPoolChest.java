package com.melonstudios.bmnw.hardcoded.lootpool.coded;

import com.melonstudios.bmnw.hardcoded.lootpool.LootPool;
import com.melonstudios.bmnw.hardcoded.lootpool.StackPoolEntry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class LootPoolChest extends LootPool<ItemStack> {
    private final StackPoolEntry[] entries;
    private final int totalWeight;
    private final float chance;

    public LootPoolChest(float chance, StackPoolEntry... entries) {
        if (entries.length == 0) throw new IllegalArgumentException("Cannot have an empty loot pool!");
        this.chance = chance;
        this.entries = entries;
        int w = 0;
        for (StackPoolEntry entry : entries) {
            w += entry.getWeight();
        }
        this.totalWeight = w;
    }
    public LootPoolChest(StackPoolEntry... entries) {
        this(2, entries);
    }

    @Override
    public ItemStack get(Random random) {
        if (random.nextFloat() < chance) {
            int i = random.nextInt(this.totalWeight);
            int c = 0;
            for (StackPoolEntry entry : this.entries) {
                c += entry.getWeight();
                if (i < c) return entry.getStack(random);
            }
        }
        return ItemStack.EMPTY;
    }
}
