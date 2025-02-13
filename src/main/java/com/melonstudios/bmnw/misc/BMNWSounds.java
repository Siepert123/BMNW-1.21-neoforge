package com.melonstudios.bmnw.misc;

import com.melonstudios.bmnw.BMNW;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BMNWSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "bmnw");

    public static final DeferredHolder<SoundEvent, SoundEvent> GEIGER_CLICK = SOUND_EVENTS.register(
            "geiger_click",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "geiger_click"), 8)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_EXPLOSION = SOUND_EVENTS.register(
            "large_explosion",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "large_explosion"), 128)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> SMALL_EXPLOSION = SOUND_EVENTS.register(
            "small_explosion",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "small_explosion"), 64)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_OPEN = SOUND_EVENTS.register(
            "door_lol_open",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("door_lol_open"), 16)
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_CLOSE = SOUND_EVENTS.register(
            "door_lol_close",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("door_lol_close"), 16)
    );

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}