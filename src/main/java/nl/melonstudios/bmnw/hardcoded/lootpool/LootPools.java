package nl.melonstudios.bmnw.hardcoded.lootpool;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import nl.melonstudios.bmnw.hardcoded.lootpool.coded.LootPoolItemStack;
import nl.melonstudios.bmnw.hardcoded.lootpool.coded.LootPoolStateSupplier;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWItems;

public class LootPools {
    public static final LootPoolItemStack CHEST_RADIO_ANTENNA = new LootPoolItemStack(
            0.2f,
            new StackPoolEntry(BMNWItems.STEEL_INGOT.get(), 1, 3, 10),
            new StackPoolEntry(BMNWItems.TITANIUM_INGOT.get(), 1, 3, 7),
            new StackPoolEntry(Items.IRON_INGOT, 1, 3, 10),
            new StackPoolEntry(BMNWItems.STEEL_TRIPOLE.get(), 1, 3, 10),
            new StackPoolEntry(BMNWItems.ANTENNA_DISH.get(), 1, 3, 3),
            new StackPoolEntry(BMNWItems.ANTENNA_TOP.get(), 1, 1, 1),
            new StackPoolEntry(BMNWItems.BASIC_CIRCUIT.get(), 1, 1, 1)
    );
    public static final LootPoolItemStack CHEST_BRICK_BUILDING_SECRET = new LootPoolItemStack(
           0.5f,
           new StackPoolEntry(BMNWItems.STEEL_INGOT.get(), 2,5, 15),
           new StackPoolEntry(BMNWItems.TITANIUM_INGOT.get(), 1,3, 15),
           new StackPoolEntry(BMNWItems.BASIC_CIRCUIT.get(), 1,3, 5),
           new StackPoolEntry(BMNWItems.ENHANCED_CIRCUIT.get(), 1,2, 3),
           new StackPoolEntry(BMNWItems.COPPER_WIRE.get(), 8,16, 10),
           new StackPoolEntry(BMNWItems.CONDUCTIVE_COPPER_WIRE.get(), 8,16, 10)
    );
    public static final LootPool<BlockState> STATE_ANCIENT_MUSEUM = new LootPoolStateSupplier(
            new StateSupplierPoolEntry((random -> Blocks.HEAVY_CORE.defaultBlockState()), 1),
            new StateSupplierPoolEntry((random -> Blocks.POTTED_OAK_SAPLING.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> Blocks.POTTED_BIRCH_SAPLING.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> Blocks.AMETHYST_CLUSTER.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> Blocks.MOSS_BLOCK.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> Blocks.WHITE_WOOL.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> Blocks.RED_WOOL.defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> BMNWBlocks.ANTENNA_TOP.get().defaultBlockState()), 100),
            new StateSupplierPoolEntry((random -> BMNWBlocks.ANTENNA_DISH.get().defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(random.nextInt(4)))), 100),
            new StateSupplierPoolEntry((random -> BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(random.nextInt(4)))), 100),
            new StateSupplierPoolEntry((random -> Blocks.IRON_BLOCK.defaultBlockState()), 25),
            new StateSupplierPoolEntry((random -> BMNWBlocks.STEEL_BLOCK.get().defaultBlockState()), 25)
    );
    public static final LootPool<ItemStack> DROPS_METEORITE_TREASURE = new LootPoolItemStack(
            new StackPoolEntry(BMNWItems.PLUTONIUM_238_INGOT.get(), 1, 1, 25)
    );
}
