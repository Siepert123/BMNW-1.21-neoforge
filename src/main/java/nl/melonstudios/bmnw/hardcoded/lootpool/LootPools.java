package nl.melonstudios.bmnw.hardcoded.lootpool;

import nl.melonstudios.bmnw.hardcoded.lootpool.coded.LootPoolChest;
import nl.melonstudios.bmnw.item.BMNWItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LootPools {
    public static final LootPool<ItemStack> CHEST_RADIO_ANTENNA = new LootPoolChest(
            0.2f,
            new StackPoolEntry(BMNWItems.STEEL_INGOT.get(), 1, 3, 10),
            new StackPoolEntry(BMNWItems.TITANIUM_INGOT.get(), 1, 3, 7),
            new StackPoolEntry(Items.IRON_INGOT, 1, 3, 10),
            new StackPoolEntry(BMNWItems.STEEL_TRIPOLE.get(), 1, 3, 10),
            new StackPoolEntry(BMNWItems.ANTENNA_DISH.get(), 1, 3, 3),
            new StackPoolEntry(BMNWItems.ANTENNA_TOP.get(), 1, 1, 1),
            new StackPoolEntry(BMNWItems.BASIC_CIRCUIT.get(), 1, 1, 1)
    );
    public static final LootPool<ItemStack> CHEST_BRICK_BUILDING_SECRET = new LootPoolChest(
           0.5f,
           new StackPoolEntry(BMNWItems.STEEL_INGOT.get(), 2,5, 15),
           new StackPoolEntry(BMNWItems.TITANIUM_INGOT.get(), 1,3, 15),
           new StackPoolEntry(BMNWItems.BASIC_CIRCUIT.get(), 1,3, 5),
           new StackPoolEntry(BMNWItems.ENHANCED_CIRCUIT.get(), 1,2, 3),
           new StackPoolEntry(BMNWItems.COPPER_WIRE.get(), 8,16, 10),
           new StackPoolEntry(BMNWItems.CONDUCTIVE_COPPER_WIRE.get(), 8,16, 10)
    );
}
