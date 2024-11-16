package com.siepert.bmnw.item;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.effect.ModEffects;
import com.siepert.bmnw.item.custom.*;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("bmnw");

    public static final DeferredItem<Item> PLAYSTATION = ITEMS.register("playstation",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_BLOCK = ITEMS.register("steel_block",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> IRON_PLATE = ITEMS.register("iron_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.register("copper_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.register("gold_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_PLATE = ITEMS.register("steel_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_WIRE = ITEMS.register("iron_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_WIRE = ITEMS.register("gold_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_WIRE = ITEMS.register("steel_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<BlockItem> URANIUM_ORE = ITEMS.register("uranium_ore",
            () -> new BlockItem(ModBlocks.URANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> THORIUM_ORE = ITEMS.register("thorium_ore",
            () -> new BlockItem(ModBlocks.THORIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_URANIUM_ORE = ITEMS.register("deepslate_uranium_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_URANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_THORIUM_ORE = ITEMS.register("deepslate_thorium_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_THORIUM_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_THORIUM = ITEMS.register("raw_thorium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> RAW_URANIUM_BLOCK = ITEMS.register("raw_uranium_block",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.RAW_URANIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> RAW_THORIUM_BLOCK = ITEMS.register("raw_thorium_block",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.RAW_THORIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_INGOT = ITEMS.register("uranium_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), UnitConvertor.fromNano(300)));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_INGOT = ITEMS.register("thorium_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), UnitConvertor.fromMicro(1)));
    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_BLOCK = ITEMS.register("uranium_block",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.URANIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> THORIUM_BLOCK = ITEMS.register("thorium_block",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.THORIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<GeigerCounterItem> GEIGER_COUNTER = ITEMS.register("geiger_counter",
            () -> new GeigerCounterItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> DECONTAMINATOR = ITEMS.register("decontaminator",
            () -> new BlockItem(ModBlocks.DECONTAMINATOR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> NUCLEAR_CHARGE = ITEMS.register("nuclear_charge",
            () -> new BlockItem(ModBlocks.NUCLEAR_CHARGE.get(), new Item.Properties()));
    public static final DeferredItem<DetonatorItem> DETONATOR = ITEMS.register("detonator",
            () -> new DetonatorItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> SLAKED_NUCLEAR_REMAINS = ITEMS.register("slaked_nuclear_remains",
            () -> new BlockItem(ModBlocks.SLAKED_NUCLEAR_REMAINS.get(), new Item.Properties()));
    public static final DeferredItem<NuclearRemainsBlockItem> NUCLEAR_REMAINS = ITEMS.register("nuclear_remains",
            () -> new NuclearRemainsBlockItem(ModBlocks.NUCLEAR_REMAINS.get(), new Item.Properties()));
    public static final DeferredItem<NuclearRemainsBlockItem> BLAZING_NUCLEAR_REMAINS = ITEMS.register("blazing_nuclear_remains",
            () -> new NuclearRemainsBlockItem(ModBlocks.BLAZING_NUCLEAR_REMAINS.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CHARRED_LOG = ITEMS.register("charred_log",
            () -> new BlockItem(ModBlocks.CHARRED_LOG.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CHARRED_PLANKS = ITEMS.register("charred_planks",
            () -> new BlockItem(ModBlocks.CHARRED_PLANKS.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_GRASS_BLOCK = ITEMS.register("irradiated_grass_block",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_GRASS_BLOCK.get(), new Item.Properties(), UnitConvertor.fromPico(2)));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAVES = ITEMS.register("irradiated_leaves",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_LEAVES.get(), new Item.Properties(), UnitConvertor.fromPico(2)));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAF_PILE = ITEMS.register("irradiated_leaf_pile",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_LEAF_PILE.get(), new Item.Properties(), UnitConvertor.fromPico(2)));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_PLANT = ITEMS.register("irradiated_plant",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_PLANT.get(), new Item.Properties(), UnitConvertor.fromPico(2)));
    public static final DeferredItem<SimpleRadioactiveItem> IRRADIATED_PLANT_FIBERS = ITEMS.register("irradiated_plant_fibers",
            () -> new SimpleRadioactiveItem(new Item.Properties().food(
                    new FoodProperties(1, 0, false,
                            0.1f, Optional.empty(), List.of(new FoodProperties.PossibleEffect(
                            () -> new MobEffectInstance(ModEffects.CONTAMINATION, 10, 0), 1
                    )))
            ), UnitConvertor.fromPico(2)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
