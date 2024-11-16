package com.siepert.bmnw.block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("bmnw");


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
