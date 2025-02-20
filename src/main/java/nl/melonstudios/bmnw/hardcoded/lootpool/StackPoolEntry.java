package nl.melonstudios.bmnw.hardcoded.lootpool;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

public class StackPoolEntry {
    private final Item item;
    private final int min, max;
    private final int weight;

    public StackPoolEntry(Item item, int min, int max) {
        this(item, min, max, 1);
    }
    public StackPoolEntry(Item item, int min, int max, int weight) {
        if (weight < 1) throw new IllegalArgumentException("Loot pool entry weight must be above 0!");
        this.item = item;
        this.min = min;
        this.max = max;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    public ItemStack getStack(Random random) {
        return new ItemStack(this.item, random.nextInt(this.min, this.max+1));
    }

    public CompoundTag serialize(HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("min", this.min);
        nbt.putInt("max", this.max);
        nbt.putInt("weight", this.weight);
        nbt.putInt("item", Item.getId(this.item));

        return nbt;
    }
    public static StackPoolEntry deserialize(CompoundTag nbt, HolderLookup.Provider registries) {
        return new StackPoolEntry(
                Item.byId(nbt.getInt("item")),
                nbt.getInt("min"),
                nbt.getInt("max"),
                nbt.getInt("weight")
        );
    }
}
