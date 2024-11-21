package com.siepert.bmnw.misc;

import com.siepert.bmnw.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Random;
import java.util.function.Supplier;

import static com.siepert.bmnw.item.ModItems.*;

public class ModTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "bmnw");

    @Deprecated(forRemoval = true)
    public static final Supplier<CreativeModeTab> MAIN = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.main"))
                    .icon(() -> new ItemStack(PLAYSTATION.get()))
                    .displayItems((parameters, items) -> {
                        //FIXME: either remove or add EVERY SINGLE ITEM here.
                    })
                    .build()
    );

    private static Supplier<ItemStack> getBlocksIcon() {
        return () -> new ItemStack(CONCRETE_BRICKS.get());
    }
    @NoUnused
    public static final Supplier<CreativeModeTab> BLOCKS = CREATIVE_TABS.register("blocks",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.blocks"))
                    .icon(getBlocksIcon())
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
                    .displayItems((parameters, items) -> {
                        items.accept(STEEL_INGOT);
                        items.accept(STEEL_BLOCK);

                        items.accept(IRON_PLATE);
                        items.accept(COPPER_PLATE);
                        items.accept(GOLD_PLATE);
                        items.accept(STEEL_PLATE);

                        items.accept(IRON_WIRE);
                        items.accept(COPPER_WIRE);
                        items.accept(GOLD_WIRE);
                        items.accept(STEEL_WIRE);

                        items.accept(TUNGSTEN_ORE);
                        items.accept(TITANIUM_ORE);
                        items.accept(URANIUM_ORE);
                        items.accept(THORIUM_ORE);
                        items.accept(DEEPSLATE_TUNGSTEN_ORE);
                        items.accept(DEEPSLATE_TITANIUM_ORE);
                        items.accept(DEEPSLATE_URANIUM_ORE);
                        items.accept(DEEPSLATE_THORIUM_ORE);
                        items.accept(RAW_TUNGSTEN);
                        items.accept(RAW_TITANIUM);
                        items.accept(RAW_URANIUM);
                        items.accept(RAW_THORIUM);
                        items.accept(RAW_TUNGSTEN_BLOCK);
                        items.accept(RAW_TITANIUM_BLOCK);
                        items.accept(RAW_URANIUM_BLOCK);
                        items.accept(RAW_THORIUM_BLOCK);
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
                        items.accept(TUNGSTEN_BLOCK);
                        items.accept(TITANIUM_BLOCK);
                        items.accept(URANIUM_BLOCK);
                        items.accept(URANIUM_233_BLOCK);
                        items.accept(URANIUM_235_BLOCK);
                        items.accept(URANIUM_238_BLOCK);
                        items.accept(URANIUM_FUEL_BLOCK);
                        items.accept(THORIUM_BLOCK);
                        items.accept(THORIUM_FUEL_BLOCK);
                    })
                    .build()
    );

    @NoUnused
    public static final Supplier<CreativeModeTab> BOMBS = CREATIVE_TABS.register("bombs",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.bombs"))
                    .icon(() -> new ItemStack(EXAMPLE_MISSILE.get()))
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

    @NoUnused
    public static final Supplier<CreativeModeTab> TOOLS = CREATIVE_TABS.register("tools",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.tools"))
                    .icon(() -> new ItemStack(URANIUM_SANDWICH.get()))
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
                    .displayItems((parameters, items) -> {
                        items.accept(DECONTAMINATOR);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
