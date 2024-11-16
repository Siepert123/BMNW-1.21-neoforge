package com.siepert.bmnw.item;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.item.custom.GeigerCounterItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("bmnw");

    public static final DeferredItem<Item> PLAYSTATION = ITEMS.register("playstation",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<GeigerCounterItem> GEIGER_COUNTER = ITEMS.register("geiger_counter",
            () -> new GeigerCounterItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> CHARRED_LOG = ITEMS.register("charred_log",
            () -> new BlockItem(ModBlocks.CHARRED_LOG.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CHARRED_PLANKS = ITEMS.register("charred_planks",
            () -> new BlockItem(ModBlocks.CHARRED_PLANKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> IRRADIATED_GRASS_BLOCK = ITEMS.register("irradiated_grass_block",
            () -> new BlockItem(ModBlocks.IRRADIATED_GRASS_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> IRRADIATED_LEAVES = ITEMS.register("irradiated_leaves",
            () -> new BlockItem(ModBlocks.IRRADIATED_LEAVES.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> IRRADIATED_LEAF_PILE = ITEMS.register("irradiated_leaf_pile",
            () -> new BlockItem(ModBlocks.IRRADIATED_LEAF_PILE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> IRRADIATED_PLANT = ITEMS.register("irradiated_plant",
            () -> new BlockItem(ModBlocks.IRRADIATED_PLANT.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
