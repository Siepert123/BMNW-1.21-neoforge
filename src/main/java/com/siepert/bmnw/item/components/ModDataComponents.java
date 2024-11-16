package com.siepert.bmnw.item.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("removal")
public class ModDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents("bmnw");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> TARGET = COMPONENTS.registerComponentType(
            "target",
            builder -> builder
                    .persistent(BlockPos.CODEC)
                    .networkSynchronized(BlockPos.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
