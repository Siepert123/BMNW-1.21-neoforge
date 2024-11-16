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
                        output.accept(GEIGER_COUNTER);

                        output.accept(CHARRED_LOG);
                        output.accept(CHARRED_PLANKS);
                        output.accept(IRRADIATED_GRASS_BLOCK);
                        output.accept(IRRADIATED_LEAVES);
                        output.accept(IRRADIATED_LEAF_PILE);
                        output.accept(IRRADIATED_PLANT);
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
