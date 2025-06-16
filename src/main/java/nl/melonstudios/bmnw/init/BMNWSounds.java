package nl.melonstudios.bmnw.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;
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

    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_OPEN = createDefaultSound("door_lol_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_CLOSE = createDefaultSound("door_lol_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_LOL_3 = createDefaultSound("door_lol_3");

    public static final DeferredHolder<SoundEvent, SoundEvent> GENERIC_GUNSHOT = SOUND_EVENTS.register(
            "generic_gunshot",
            () -> SoundEvent.createFixedRangeEvent(BMNW.namespace("generic_gunshot"), 64)
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_OPEN = createDefaultSound("hatch_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_CLOSE = createDefaultSound("hatch_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_OPEN_FULL = createDefaultSound("hatch_open_full");
    public static final DeferredHolder<SoundEvent, SoundEvent> HATCH_CLOSE_FULL = createDefaultSound("hatch_close_full");

    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_LOCKABLE_DOOR_OPEN = createDefaultSound("metal_lock_door_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_LOCKABLE_DOOR_CLOSE = createDefaultSound("metal_lock_door_close");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOCK = createDefaultSound("lock");

    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_SLIDING_DOOR = createDefaultSound("metal_sliding_door");

    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDING_BLAST_DOOR_OPEN =
            createDefaultSound("sliding_blast_door_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDING_BLAST_DOOR_CLOSE =
            createDefaultSound("sliding_blast_door_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_DOOR_OPEN =
            createDefaultSound("large_door_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_DOOR_CLOSE =
            createDefaultSound("large_door_close");

    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_RODS_LOWER =
            createDefaultSound("metal_rods_lower");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_RODS_ELEVATE =
            createDefaultSound("metal_rods_elevate");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMALL_MACHINE_ROTATE =
            createDefaultSound("small_machine_rotate");

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_GEARS_START =
            createDefaultSound("large_gears_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_GEARS_LOOP =
            createDefaultSound("large_gears_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_GEARS_STOP =
            createDefaultSound("large_gears_stop");

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_SHREDDER_FAIL =
            createDefaultSound("large_shredder_fail");

    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_SHREDDER_START =
            createDefaultSound("large_shredder_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_SHREDDER_LOOP =
            createDefaultSound("large_shredder_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_SHREDDER_STOP =
            createDefaultSound("large_shredder_stop");

    public static final DeferredHolder<SoundEvent, SoundEvent> EXTENDABLE_CATWALK_EXTEND =
            createDefaultSound("extendable_catwalk_extend");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXTENDABLE_CATWALK_RETRACT =
            createDefaultSound("extendable_catwalk_retract");

    public static class Ambient {
        public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_GENERATOR_IN_OFF =
                createDefaultSound("ambient.large_generator_in_off");
        public static final DeferredHolder<SoundEvent, SoundEvent> LARGE_GENERATOR_IN_ON =
                createDefaultSound("ambient.large_generator_in_on");
    }

    public static class Block {
        public static final DeferredHolder<SoundEvent, SoundEvent> PIPE =
                createDefaultSound("block.pipe");
    }

    public static class Music {
        public static final DeferredHolder<SoundEvent, SoundEvent> STEVE_FIGHT =
                createDefaultSound("music.steve_fight");
    }

    private static DeferredHolder<SoundEvent, SoundEvent> createDefaultSound(String name) {
        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(BMNW.namespace(name))
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