package com.siepert.bmnw.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("bmnw");



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
