package nl.melonstudios.bmnw.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.BMNW;

public class BMNWSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, "bmnw");

    public static final DeferredHolder<SoundEvent, SoundEvent> UI_WOOD_CLICK = SOUND_EVENTS.register(
            "ui_wood_click",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("ui_wood_click"), 8)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> GEIGER_CLICK = SOUND_EVENTS.register(
            "geiger_click",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "geiger_click"), 8)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_EXPLOSION = SOUND_EVENTS.register(
            "large_explosion",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "large_explosion"), 128)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> BOMB_1 = SOUND_EVENTS.register(
            "bomb_1",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "bomb_1"), 64)
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> BOMB_2 = SOUND_EVENTS.register(
            "bomb_2",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "bomb_2"), 64)
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> BOMB_3 = SOUND_EVENTS.register(
            "bomb_3",
            () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath("bmnw", "bomb_3"), 64)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_OPEN = SOUND_EVENTS.register(
            "door_lol_open",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("door_lol_open"), 16)
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_CLOSE = SOUND_EVENTS.register(
            "door_lol_close",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("door_lol_close"), 16)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_3 = SOUND_EVENTS.register(
            "door_lol_3",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("door_lol_3"), 16)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> GENERIC_GUNSHOT = SOUND_EVENTS.register(
            "generic_gunshot",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("generic_gunshot"), 64)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_OPEN = SOUND_EVENTS.register(
            "hatch_open",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("hatch_open"), 16)
    );
    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_CLOSE = createDefaultSound("hatch_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDING_BLAST_DOOR_OPEN =
            createDefaultSound("sliding_blast_door_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDING_BLAST_DOOR_CLOSE =
            createDefaultSound("sliding_blast_door_close");

    private static DeferredHolder<SoundEvent, SoundEvent> createDefaultSound(String name) {
        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createFixedRangeEvent(BMNW.namespace(name), 16)
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playClick() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1)
        );
    }
    @OnlyIn(Dist.CLIENT)
    public static void playClickOld() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(UI_WOOD_CLICK.get(), 1, 1)
        );
    }
}