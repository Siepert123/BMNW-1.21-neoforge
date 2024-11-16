package com.siepert.bmnw.item.components;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("removal")
public class ModDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents("bmnw");

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
