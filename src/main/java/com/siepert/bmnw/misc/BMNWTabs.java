package com.siepert.bmnw.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Random;
import java.util.function.Supplier;

import static com.siepert.bmnw.item.BMNWItems.*;

public class BMNWTabs {
    private static void addItems(CreativeModeTab.Output items, ItemLike... itemLikes) {
        for (ItemLike itemLike : itemLikes) {
            items.accept(itemLike);
        }
    }

    //General item order:
    //iron copper gold conductive_copper lead tungsten titanium steel uranium thorium plutonium
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "bmnw");

    private static Supplier<ItemStack> getBlocksIcon() {
        return () -> new ItemStack(CONCRETE_BRICKS.get());
    }
    @NoUnused
    public static final Supplier<CreativeModeTab> BLOCKS = CREATIVE_TABS.register("blocks",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.blocks"))
                    .icon(getBlocksIcon())
                    .withTabsBefore(
                            CreativeModeTabs.BUILDING_BLOCKS.location(),
                            CreativeModeTabs.COLORED_BLOCKS.location(),
                            CreativeModeTabs.NATURAL_BLOCKS.location(),
                            CreativeModeTabs.FUNCTIONAL_BLOCKS.location(),
                            CreativeModeTabs.REDSTONE_BLOCKS.location(),
                            CreativeModeTabs.TOOLS_AND_UTILITIES.location(),
                            CreativeModeTabs.COMBAT.location(),
                            CreativeModeTabs.FOOD_AND_DRINKS.location(),
                            CreativeModeTabs.INGREDIENTS.location(),
                            CreativeModeTabs.SPAWN_EGGS.location()
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:materials"),
                            ResourceLocation.parse("bmnw:tools"),
                            ResourceLocation.parse("bmnw:machines"),
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {
                        items.accept(CONCRETE);
                        items.accept(CONCRETE_STAIRS);
                        items.accept(CONCRETE_SLAB);
                        items.accept(CONCRETE_BRICKS);
                        items.accept(CONCRETE_BRICKS_STAIRS);
                        items.accept(CONCRETE_BRICKS_SLAB);
                        items.accept(FOUNDATION_CONCRETE);
                        items.accept(CHISELED_CONCRETE_BRICKS);
                        items.accept(STEEL_REINFORCED_GLASS);
                        items.accept(CREATIVE_CONCRETE_BRICKS);

                        addItems(items,
                                LEAD_ORE,
                                TUNGSTEN_ORE,
                                TITANIUM_ORE,
                                URANIUM_ORE,
                                THORIUM_ORE,
                                DEEPSLATE_LEAD_ORE,
                                DEEPSLATE_TUNGSTEN_ORE,
                                DEEPSLATE_TITANIUM_ORE,
                                DEEPSLATE_URANIUM_ORE,
                                DEEPSLATE_THORIUM_ORE
                        );

                        addItems(items,
                                RAW_LEAD_BLOCK,
                                RAW_TUNGSTEN_BLOCK,
                                RAW_TITANIUM_BLOCK,
                                RAW_URANIUM_BLOCK,
                                RAW_THORIUM_BLOCK,
                                CONDUCTIVE_COPPER_BLOCK,
                                LEAD_BLOCK,
                                TUNGSTEN_BLOCK,
                                TITANIUM_BLOCK,
                                STEEL_BLOCK,
                                URANIUM_BLOCK,
                                URANIUM_233_BLOCK,
                                URANIUM_235_BLOCK,
                                URANIUM_238_BLOCK,
                                URANIUM_FUEL_BLOCK,
                                THORIUM_BLOCK,
                                THORIUM_FUEL_BLOCK,
                                PLUTONIUM_BLOCK,
                                PLUTONIUM_238_BLOCK,
                                PLUTONIUM_239_BLOCK,
                                PLUTONIUM_240_BLOCK,
                                PLUTONIUM_241_BLOCK,
                                REACTOR_GRADE_PLUTONIUM_BLOCK,
                                PLUTONIUM_FUEL_BLOCK
                        );

                        items.accept(SLAKED_NUCLEAR_REMAINS);
                        items.accept(NUCLEAR_REMAINS);
                        items.accept(BLAZING_NUCLEAR_REMAINS);

                        items.accept(CHARRED_LOG);
                        items.accept(CHARRED_PLANKS);
                        items.accept(IRRADIATED_GRASS_BLOCK);
                        items.accept(IRRADIATED_LEAVES);
                        items.accept(IRRADIATED_LEAF_PILE);
                        items.accept(IRRADIATED_PLANT);

                        items.accept(NUCLEAR_WASTE_BARREL);
                    })
                    .build()
    );

    private static Supplier<ItemStack> getMaterialsIcon() {
        Random random = new Random();
        return switch (random.nextInt(5)) {
            case 0 -> () -> new ItemStack(STEEL_INGOT.get());
            case 1 -> () -> new ItemStack(URANIUM_INGOT.get());
            case 2 -> () -> new ItemStack(TUNGSTEN_INGOT.get());
            case 3 -> () -> new ItemStack(TITANIUM_INGOT.get());
            case 4 -> () -> new ItemStack(THORIUM_INGOT.get());
            default -> () -> new ItemStack(PLAYSTATION.get());
        };
    }

    @NoUnused
    public static final Supplier<CreativeModeTab> MATERIALS = CREATIVE_TABS.register("materials",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.materials"))
                    .icon(getMaterialsIcon())
                    .withTabsBefore(
                            CreativeModeTabs.BUILDING_BLOCKS.location(),
                            CreativeModeTabs.COLORED_BLOCKS.location(),
                            CreativeModeTabs.NATURAL_BLOCKS.location(),
                            CreativeModeTabs.FUNCTIONAL_BLOCKS.location(),
                            CreativeModeTabs.REDSTONE_BLOCKS.location(),
                            CreativeModeTabs.TOOLS_AND_UTILITIES.location(),
                            CreativeModeTabs.COMBAT.location(),
                            CreativeModeTabs.FOOD_AND_DRINKS.location(),
                            CreativeModeTabs.INGREDIENTS.location(),
                            CreativeModeTabs.SPAWN_EGGS.location(),
                            ResourceLocation.parse("bmnw:blocks")
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:tools"),
                            ResourceLocation.parse("bmnw:machines"),
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {items.accept(RAW_LEAD);
                        items.accept(RAW_TUNGSTEN);
                        items.accept(RAW_TITANIUM);
                        items.accept(RAW_URANIUM);
                        items.accept(RAW_THORIUM);

                        items.accept(LEAD_NUGGET);
                        items.accept(TUNGSTEN_NUGGET);
                        items.accept(TITANIUM_NUGGET);
                        items.accept(URANIUM_NUGGET);
                        items.accept(URANIUM_233_NUGGET);
                        items.accept(URANIUM_235_NUGGET);
                        items.accept(URANIUM_238_NUGGET);
                        items.accept(URANIUM_FUEL_NUGGET);
                        items.accept(THORIUM_NUGGET);
                        items.accept(THORIUM_FUEL_NUGGET);
                        items.accept(PLUTONIUM_NUGGET);
                        items.accept(PLUTONIUM_238_NUGGET);
                        items.accept(PLUTONIUM_239_NUGGET);
                        items.accept(PLUTONIUM_240_NUGGET);
                        items.accept(PLUTONIUM_241_NUGGET);
                        items.accept(REACTOR_GRADE_PLUTONIUM_NUGGET);
                        items.accept(PLUTONIUM_FUEL_NUGGET);

                        items.accept(CONDUCTIVE_COPPER_INGOT);
                        items.accept(LEAD_INGOT);
                        items.accept(TUNGSTEN_INGOT);
                        items.accept(TITANIUM_INGOT);
                        items.accept(STEEL_INGOT);
                        items.accept(URANIUM_INGOT);
                        items.accept(URANIUM_233_INGOT);
                        items.accept(URANIUM_235_INGOT);
                        items.accept(URANIUM_238_INGOT);
                        items.accept(URANIUM_FUEL_INGOT);
                        items.accept(THORIUM_INGOT);
                        items.accept(THORIUM_FUEL_INGOT);
                        items.accept(PLUTONIUM_INGOT);
                        items.accept(PLUTONIUM_238_INGOT);
                        items.accept(PLUTONIUM_239_INGOT);
                        items.accept(PLUTONIUM_240_INGOT);
                        items.accept(PLUTONIUM_241_INGOT);
                        items.accept(REACTOR_GRADE_PLUTONIUM_INGOT);
                        items.accept(PLUTONIUM_FUEL_INGOT);

                        items.accept(IRON_PLATE);
                        items.accept(COPPER_PLATE);
                        items.accept(GOLD_PLATE);
                        items.accept(CONDUCTIVE_COPPER_PLATE);
                        items.accept(LEAD_PLATE);
                        items.accept(TUNGSTEN_PLATE);
                        items.accept(TITANIUM_PLATE);
                        items.accept(STEEL_PLATE);

                        items.accept(IRON_WIRE);
                        items.accept(COPPER_WIRE);
                        items.accept(GOLD_WIRE);
                        items.accept(CONDUCTIVE_COPPER_WIRE);
                        items.accept(STEEL_WIRE);

                        items.accept(URANIUM_BILLET);
                        items.accept(URANIUM_233_BILLET);
                        items.accept(URANIUM_235_BILLET);
                        items.accept(URANIUM_238_BILLET);
                        items.accept(URANIUM_FUEL_BILLET);
                        items.accept(THORIUM_BILLET);
                        items.accept(THORIUM_FUEL_BILLET);
                        items.accept(PLUTONIUM_BILLET);
                        items.accept(PLUTONIUM_238_BILLET);
                        items.accept(PLUTONIUM_239_BILLET);
                        items.accept(PLUTONIUM_240_BILLET);
                        items.accept(PLUTONIUM_241_BILLET);
                        items.accept(REACTOR_GRADE_PLUTONIUM_BILLET);
                        items.accept(PLUTONIUM_FUEL_BILLET);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> TOOLS = CREATIVE_TABS.register("tools",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.tools"))
                    .icon(() -> new ItemStack(URANIUM_SANDWICH.get()))
                    .withTabsBefore(
                            CreativeModeTabs.BUILDING_BLOCKS.location(),
                            CreativeModeTabs.COLORED_BLOCKS.location(),
                            CreativeModeTabs.NATURAL_BLOCKS.location(),
                            CreativeModeTabs.FUNCTIONAL_BLOCKS.location(),
                            CreativeModeTabs.REDSTONE_BLOCKS.location(),
                            CreativeModeTabs.TOOLS_AND_UTILITIES.location(),
                            CreativeModeTabs.COMBAT.location(),
                            CreativeModeTabs.FOOD_AND_DRINKS.location(),
                            CreativeModeTabs.INGREDIENTS.location(),
                            CreativeModeTabs.SPAWN_EGGS.location(),
                            ResourceLocation.parse("bmnw:blocks"),
                            ResourceLocation.parse("bmnw:materials")
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:machines"),
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {
                        items.accept(GEIGER_COUNTER);
                        items.accept(DETONATOR);
                        items.accept(TARGET_DESIGNATOR);
                        items.accept(LASER_TARGET_DESIGNATOR);
                        items.accept(EXCAVATION_VEIN_DETECTOR);
                        items.accept(EMPTY_CORE_SAMPLE);
                        items.accept(IRON_CORE_SAMPLE);
                        items.accept(COAL_CORE_SAMPLE);
                        items.accept(SOIL_CORE_SAMPLE);
                        items.accept(COPPER_CORE_SAMPLE);
                        items.accept(TUNGSTEN_CORE_SAMPLE);
                        items.accept(IRRADIATED_PLANT_FIBERS);
                        items.accept(URANIUM_SANDWICH);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> MACHINES = CREATIVE_TABS.register("machines",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.machines"))
                    .icon(() -> new ItemStack(DECONTAMINATOR.get()))
                    .withTabsBefore(
                            CreativeModeTabs.BUILDING_BLOCKS.location(),
                            CreativeModeTabs.COLORED_BLOCKS.location(),
                            CreativeModeTabs.NATURAL_BLOCKS.location(),
                            CreativeModeTabs.FUNCTIONAL_BLOCKS.location(),
                            CreativeModeTabs.REDSTONE_BLOCKS.location(),
                            CreativeModeTabs.TOOLS_AND_UTILITIES.location(),
                            CreativeModeTabs.COMBAT.location(),
                            CreativeModeTabs.FOOD_AND_DRINKS.location(),
                            CreativeModeTabs.INGREDIENTS.location(),
                            CreativeModeTabs.SPAWN_EGGS.location(),
                            ResourceLocation.parse("bmnw:blocks"),
                            ResourceLocation.parse("bmnw:materials"),
                            ResourceLocation.parse("bmnw:tools")
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {
                        //this comment removes a warning! [remove once there are 2 or more entries here]
                        items.accept(DECONTAMINATOR);
                        items.accept(TEST_EXCAVATOR);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> BOMBS = CREATIVE_TABS.register("bombs",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.bombs"))
                    .icon(() -> new ItemStack(NUCLEAR_CHARGE.get()))
                    .withTabsBefore(
                            CreativeModeTabs.BUILDING_BLOCKS.location(),
                            CreativeModeTabs.COLORED_BLOCKS.location(),
                            CreativeModeTabs.NATURAL_BLOCKS.location(),
                            CreativeModeTabs.FUNCTIONAL_BLOCKS.location(),
                            CreativeModeTabs.REDSTONE_BLOCKS.location(),
                            CreativeModeTabs.TOOLS_AND_UTILITIES.location(),
                            CreativeModeTabs.COMBAT.location(),
                            CreativeModeTabs.FOOD_AND_DRINKS.location(),
                            CreativeModeTabs.INGREDIENTS.location(),
                            CreativeModeTabs.SPAWN_EGGS.location(),
                            ResourceLocation.parse("bmnw:blocks"),
                            ResourceLocation.parse("bmnw:materials"),
                            ResourceLocation.parse("bmnw:tools"),
                            ResourceLocation.parse("bmnw:machines")
                    )
                    .displayItems((parameters, items) -> {
                        items.accept(DETONATOR);

                        items.accept(DUD);
                        items.accept(BRICK_CHARGE);
                        items.accept(NUCLEAR_CHARGE);
                        items.accept(LITTLE_BOY);
                        items.accept(CASEOH);

                        items.accept(MISSILE_LAUNCH_PAD);
                        items.accept(TARGET_DESIGNATOR);
                        items.accept(LASER_TARGET_DESIGNATOR);
                        items.accept(BASE_MISSILE);
                        items.accept(EXAMPLE_MISSILE);
                        items.accept(HE_MISSILE);
                        items.accept(NUCLEAR_MISSILE);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
