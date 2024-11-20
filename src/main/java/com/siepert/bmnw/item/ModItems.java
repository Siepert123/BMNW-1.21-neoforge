package com.siepert.bmnw.item;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.effect.ModEffects;
import com.siepert.bmnw.item.custom.*;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class ModItems {
    private static DeferredItem<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }
    @Deprecated
    private static DeferredItem<BlockItem> blockItem(String name, Block block) {
        return ITEMS.register(name, () -> new BlockItem(block, new Item.Properties()));
    }
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

    public static final DeferredItem<BlockItem> TUNGSTEN_ORE = ITEMS.register("tungsten_ore",
            () -> new BlockItem(ModBlocks.TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TITANIUM_ORE = ITEMS.register("titanium_ore",
            () -> new BlockItem(ModBlocks.TITANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_TUNGSTEN_ORE = ITEMS.register("deepslate_tungsten_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_TITANIUM_ORE = ITEMS.register("deepslate_titanium_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_TITANIUM_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> RAW_TUNGSTEN = item("raw_tungsten");
    public static final DeferredItem<Item> RAW_TITANIUM = item("raw_titanium");
    public static final DeferredItem<BlockItem> RAW_TUNGSTEN_BLOCK = ITEMS.register("raw_tungsten_block",
            () -> new BlockItem(ModBlocks.RAW_TUNGSTEN_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RAW_TITANIUM_BLOCK = ITEMS.register("raw_titanium_block",
            () -> new BlockItem(ModBlocks.RAW_TITANIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> TUNGSTEN_INGOT = item("tungsten_ingot");
    public static final DeferredItem<Item> TITANIUM_INGOT = item("titanium_ingot");
    public static final DeferredItem<BlockItem> TUNGSTEN_BLOCK = ITEMS.register("tungsten_block",
            () -> new BlockItem(ModBlocks.TUNGSTEN_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TITANIUM_BLOCK = ITEMS.register("titanium_block",
            () -> new BlockItem(ModBlocks.TITANIUM_BLOCK.get(), new Item.Properties()));

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
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.35f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_INGOT = ITEMS.register("thorium_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.1f));
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
    public static final DeferredItem<BlockItem> LITTLE_BOY = ITEMS.register("little_boy",
            () -> new BlockItem(ModBlocks.LITTLE_BOY.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> CASEOH = ITEMS.register("caseoh",
            () -> new BlockItem(ModBlocks.CASEOH.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> DUD = ITEMS.register("dud",
            () -> new BlockItem(ModBlocks.DUD.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> BRICK_CHARGE = ITEMS.register("brick_charge",
            () -> new BlockItem(ModBlocks.BRICK_CHARGE.get(), new Item.Properties()));
    public static final DeferredItem<DetonatorItem> DETONATOR = ITEMS.register("detonator",
            () -> new DetonatorItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> MISSILE_LAUNCH_PAD = ITEMS.register("missile_launch_pad",
            () -> new BlockItem(ModBlocks.MISSILE_LAUNCH_PAD.get(), new Item.Properties()));
    public static final DeferredItem<TargetDesignatorItem> TARGET_DESIGNATOR = ITEMS.register("target_designator",
            () -> new TargetDesignatorItem(new Item.Properties()));
    public static final DeferredItem<BlockItem> EXAMPLE_MISSILE = ITEMS.register("example_missile",
            () -> new BlockItem(ModBlocks.EXAMPLE_MISSILE.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> CONCRETE = ITEMS.register("concrete",
            () -> new BlockItem(ModBlocks.CONCRETE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_SLAB = ITEMS.register("concrete_slab",
            () -> new BlockItem(ModBlocks.CONCRETE_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_STAIRS = ITEMS.register("concrete_stairs",
            () -> new BlockItem(ModBlocks.CONCRETE_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS = ITEMS.register("concrete_bricks",
            () -> new BlockItem(ModBlocks.CONCRETE_BRICKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS_SLAB = ITEMS.register("concrete_bricks_slab",
            () -> new BlockItem(ModBlocks.CONCRETE_BRICKS_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS_STAIRS = ITEMS.register("concrete_bricks_stairs",
            () -> new BlockItem(ModBlocks.CONCRETE_BRICKS_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> FOUNDATION_CONCRETE = ITEMS.register("foundation_concrete",
            () -> new BlockItem(ModBlocks.FOUNDATION_CONCRETE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_REINFORCED_GLASS = ITEMS.register("steel_reinforced_glass",
            () -> new BlockItem(ModBlocks.STEEL_REINFORCED_GLASS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CREATIVE_CONCRETE_BRICKS = ITEMS.register("creative_concrete_bricks",
            () -> new BlockItem(ModBlocks.CREATIVE_CONCRETE_BRICKS.get(), new Item.Properties()));

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
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_GRASS_BLOCK.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAVES = ITEMS.register("irradiated_leaves",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_LEAVES.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAF_PILE = ITEMS.register("irradiated_leaf_pile",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_LEAF_PILE.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_PLANT = ITEMS.register("irradiated_plant",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.IRRADIATED_PLANT.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveItem> IRRADIATED_PLANT_FIBERS = ITEMS.register("irradiated_plant_fibers",
            () -> new SimpleRadioactiveItem(new Item.Properties().food(
                    new FoodProperties(1, 0, false,
                            0.1f, Optional.empty(), List.of(new FoodProperties.PossibleEffect(
                            () -> new MobEffectInstance(ModEffects.CONTAMINATION, 100, 0), 1
                    )))
            ), 0.1f)
    );

    public static final DeferredItem<SimpleRadioactiveBlockItem> NUCLEAR_WASTE_BARREL = ITEMS.register("nuclear_waste_barrel",
            () -> new SimpleRadioactiveBlockItem(ModBlocks.NUCLEAR_WASTE_BARREL.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_SANDWICH = ITEMS.register("uranium_sandwich",
            () -> new SimpleRadioactiveItem(new Item.Properties().food(new FoodProperties(19, 100, true,
                    4.0f, Optional.empty(), List.of(new FoodProperties.PossibleEffect(
                    () -> new MobEffectInstance(ModEffects.CONTAMINATION, 100, 1), 0.5f
            )))), 69)
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
