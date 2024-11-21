package com.siepert.bmnw.misc;

import com.siepert.bmnw.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.siepert.bmnw.item.ModItems.*;

public class ModTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "bmnw");

    public static final Supplier<CreativeModeTab> MAIN = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.main"))
                    .icon(() -> new ItemStack(PLAYSTATION.get()))
                    .displayItems((parameters, items) -> {
                        items.accept(GEIGER_COUNTER);
                        items.accept(DECONTAMINATOR);

                        items.accept(DETONATOR);
                        items.accept(NUCLEAR_CHARGE);
                        items.accept(LITTLE_BOY);
                        items.accept(CASEOH);
                        items.accept(DUD);
                        items.accept(BRICK_CHARGE);

                        items.accept(MISSILE_LAUNCH_PAD);
                        items.accept(TARGET_DESIGNATOR);
                        items.accept(EXAMPLE_MISSILE);

                        items.accept(CONCRETE);
                        items.accept(CONCRETE_SLAB);
                        items.accept(CONCRETE_STAIRS);
                        items.accept(CONCRETE_BRICKS);
                        items.accept(CONCRETE_BRICKS_SLAB);
                        items.accept(CONCRETE_BRICKS_STAIRS);
                        items.accept(FOUNDATION_CONCRETE);
                        items.accept(STEEL_REINFORCED_GLASS);
                        items.accept(CREATIVE_CONCRETE_BRICKS);

                        items.accept(BLAZING_NUCLEAR_REMAINS);
                        items.accept(NUCLEAR_REMAINS);
                        items.accept(SLAKED_NUCLEAR_REMAINS);

                        items.accept(CHARRED_LOG);
                        items.accept(CHARRED_PLANKS);
                        items.accept(IRRADIATED_GRASS_BLOCK);
                        items.accept(IRRADIATED_LEAVES);
                        items.accept(IRRADIATED_LEAF_PILE);
                        items.accept(IRRADIATED_PLANT);
                        items.accept(IRRADIATED_PLANT_FIBERS);
                        items.accept(NUCLEAR_WASTE_BARREL);
                        items.accept(URANIUM_SANDWICH);
                    })
                    .build()
    );

    public static final Supplier<CreativeModeTab> MATERIALS = CREATIVE_TABS.register("materials",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bmnw.materials"))
                    .icon(() -> new ItemStack(URANIUM_INGOT.get()))
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
                        items.accept(TUNGSTEN_INGOT);
                        items.accept(TITANIUM_INGOT);
                        items.accept(URANIUM_INGOT);
                        items.accept(URANIUM_233_INGOT);
                        items.accept(URANIUM_235_INGOT);
                        items.accept(URANIUM_238_INGOT);
                        items.accept(THORIUM_INGOT);
                        items.accept(TUNGSTEN_BLOCK);
                        items.accept(TITANIUM_BLOCK);
                        items.accept(URANIUM_BLOCK);
                        items.accept(URANIUM_233_BLOCK);
                        items.accept(URANIUM_235_BLOCK);
                        items.accept(URANIUM_238_BLOCK);
                        items.accept(THORIUM_BLOCK);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
