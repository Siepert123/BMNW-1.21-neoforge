package com.siepert.bmnw.misc;

import com.siepert.bmnw.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Random;
import java.util.function.Supplier;

import static com.siepert.bmnw.item.ModItems.*;

public class ModTabs {
    private static void addItems(CreativeModeTab.Output items, ItemLike... itemLikes) {
        for (ItemLike itemLike : itemLikes) {
            items.accept(itemLike);
        }
    }

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
                        items.accept(STEEL_REINFORCED_GLASS);
                        items.accept(CREATIVE_CONCRETE_BRICKS);

                        addItems(items,
                                TUNGSTEN_ORE,
                                TITANIUM_ORE,
                                URANIUM_ORE,
                                THORIUM_ORE,
                                DEEPSLATE_TUNGSTEN_ORE,
                                DEEPSLATE_TITANIUM_ORE,
                                DEEPSLATE_URANIUM_ORE,
                                DEEPSLATE_THORIUM_ORE
                        );
                        {
                            ItemStack fill = new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
                            fill.set(DataComponents.MAX_STACK_SIZE, 1);
                            items.accept(fill);
                        }

                        addItems(items,
                                RAW_TUNGSTEN_BLOCK,
                                RAW_TITANIUM_BLOCK,
                                RAW_URANIUM_BLOCK,
                                RAW_THORIUM_BLOCK,
                                TUNGSTEN_BLOCK,
                                TITANIUM_BLOCK,
                                URANIUM_BLOCK,
                                THORIUM_BLOCK,
                                STEEL_BLOCK
                        );

                        addItems(items,
                                URANIUM_233_BLOCK,
                                URANIUM_235_BLOCK,
                                URANIUM_238_BLOCK,
                                URANIUM_FUEL_BLOCK,
                                THORIUM_FUEL_BLOCK
                        );

                        for (int i = 0; i < 4; i++) {
                            ItemStack fill = new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
                            fill.set(DataComponents.MAX_STACK_SIZE, 3 + i);
                            items.accept(fill);
                        }

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
                            ResourceLocation.parse("bmnw:blocks")
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:tools"),
                            ResourceLocation.parse("bmnw:machines"),
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {
                        items.accept(STEEL_INGOT);

                        items.accept(IRON_PLATE);
                        items.accept(COPPER_PLATE);
                        items.accept(GOLD_PLATE);
                        items.accept(STEEL_PLATE);

                        items.accept(IRON_WIRE);
                        items.accept(COPPER_WIRE);
                        items.accept(GOLD_WIRE);
                        items.accept(STEEL_WIRE);

                        items.accept(RAW_TUNGSTEN);
                        items.accept(RAW_TITANIUM);
                        items.accept(RAW_URANIUM);
                        items.accept(RAW_THORIUM);

                        items.accept(TUNGSTEN_NUGGET);
                        items.accept(TITANIUM_NUGGET);
                        items.accept(URANIUM_NUGGET);
                        items.accept(URANIUM_233_NUGGET);
                        items.accept(URANIUM_235_NUGGET);
                        items.accept(URANIUM_238_NUGGET);
                        items.accept(URANIUM_FUEL_NUGGET);
                        items.accept(THORIUM_NUGGET);
                        items.accept(THORIUM_FUEL_NUGGET);

                        items.accept(TUNGSTEN_INGOT);
                        items.accept(TITANIUM_INGOT);
                        items.accept(URANIUM_INGOT);
                        items.accept(URANIUM_233_INGOT);
                        items.accept(URANIUM_235_INGOT);
                        items.accept(URANIUM_238_INGOT);
                        items.accept(URANIUM_FUEL_INGOT);
                        items.accept(THORIUM_INGOT);
                        items.accept(THORIUM_FUEL_INGOT);

                        items.accept(URANIUM_BILLET);
                        items.accept(URANIUM_233_BILLET);
                        items.accept(URANIUM_235_BILLET);
                        items.accept(URANIUM_238_BILLET);
                        items.accept(URANIUM_FUEL_BILLET);
                        items.accept(THORIUM_BILLET);
                        items.accept(THORIUM_FUEL_BILLET);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> TOOLS = CREATIVE_TABS.register("tools",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.tools"))
                    .icon(() -> new ItemStack(URANIUM_SANDWICH.get()))
                    .withTabsBefore(
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
                        items.accept(IRRADIATED_PLANT_FIBERS);
                        items.accept(URANIUM_SANDWICH);
                    })
                    .build()
    );

    public static final Supplier<CreativeModeTab> MACHINES = CREATIVE_TABS.register("machines",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.machines"))
                    .icon(() -> new ItemStack(DECONTAMINATOR.get()))
                    .withTabsBefore(
                            ResourceLocation.parse("bmnw:blocks"),
                            ResourceLocation.parse("bmnw:materials"),
                            ResourceLocation.parse("bmnw:tools")
                    )
                    .withTabsAfter(
                            ResourceLocation.parse("bmnw:bombs")
                    )
                    .displayItems((parameters, items) -> {
                        items.accept(DECONTAMINATOR);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> BOMBS = CREATIVE_TABS.register("bombs",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.bombs"))
                    .icon(() -> new ItemStack(EXAMPLE_MISSILE.get()))
                    .withTabsBefore(
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
                        items.accept(EXAMPLE_MISSILE);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
