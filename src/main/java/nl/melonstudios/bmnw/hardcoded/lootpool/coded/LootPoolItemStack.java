package nl.melonstudios.bmnw.hardcoded.lootpool.coded;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPool;
import nl.melonstudios.bmnw.hardcoded.lootpool.StackPoolEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootPoolItemStack extends LootPool<ItemStack> {
    private final StackPoolEntry[] entries;
    private final int totalWeight;
    private final float chance;

    public LootPoolItemStack(float chance, StackPoolEntry... entries) {
        if (entries.length == 0) throw new IllegalArgumentException("Cannot have an empty loot pool!");
        this.chance = chance;
        this.entries = entries;
        int w = 0;
        for (StackPoolEntry entry : entries) {
            w += entry.getWeight();
        }
        this.totalWeight = w;
    }
    public LootPoolItemStack(StackPoolEntry... entries) {
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

    public CompoundTag serialize(HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("mass", this.totalWeight);
        nbt.putFloat("chance", this.chance);

        ListTag list = new ListTag();

        for (StackPoolEntry entry : this.entries) {
            list.add(entry.serialize(registries));
        }

        nbt.put("Entries", list);

        return nbt;
    }
    public static LootPoolItemStack deserialize(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = nbt.getList("Entries", Tag.TAG_COMPOUND);

        List<StackPoolEntry> entries = new ArrayList<>();

        for (Tag tag : list) {
            CompoundTag compoundTag = (CompoundTag) tag;
            entries.add(StackPoolEntry.deserialize(compoundTag, registries));
        }

        return new LootPoolItemStack(entries.toArray(new StackPoolEntry[0]));
    }
}
