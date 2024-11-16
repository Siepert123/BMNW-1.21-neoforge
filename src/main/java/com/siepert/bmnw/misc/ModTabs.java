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
                    .displayItems((parameters, output) -> {
                        output.accept(STEEL_INGOT);
                        output.accept(STEEL_BLOCK);

                        output.accept(IRON_PLATE);
                        output.accept(COPPER_PLATE);
                        output.accept(GOLD_PLATE);
                        output.accept(STEEL_PLATE);

                        output.accept(IRON_WIRE);
                        output.accept(COPPER_WIRE);
                        output.accept(GOLD_WIRE);
                        output.accept(STEEL_WIRE);

                        output.accept(URANIUM_ORE);
                        output.accept(THORIUM_ORE);
                        output.accept(DEEPSLATE_URANIUM_ORE);
                        output.accept(DEEPSLATE_THORIUM_ORE);
                        output.accept(RAW_URANIUM);
                        output.accept(RAW_THORIUM);
                        output.accept(RAW_URANIUM_BLOCK);
                        output.accept(RAW_THORIUM_BLOCK);
                        output.accept(URANIUM_INGOT);
                        output.accept(THORIUM_INGOT);
                        output.accept(URANIUM_BLOCK);
                        output.accept(THORIUM_BLOCK);

                        output.accept(GEIGER_COUNTER);
                        output.accept(DECONTAMINATOR);

                        output.accept(DETONATOR);
                        output.accept(NUCLEAR_CHARGE);

                        output.accept(BLAZING_NUCLEAR_REMAINS);
                        output.accept(NUCLEAR_REMAINS);
                        output.accept(SLAKED_NUCLEAR_REMAINS);

                        output.accept(CHARRED_LOG);
                        output.accept(CHARRED_PLANKS);
                        output.accept(IRRADIATED_GRASS_BLOCK);
                        output.accept(IRRADIATED_LEAVES);
                        output.accept(IRRADIATED_LEAF_PILE);
                        output.accept(IRRADIATED_PLANT);
                        output.accept(IRRADIATED_PLANT_FIBERS);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
