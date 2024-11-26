package com.siepert.bmnw.misc;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BMNWAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, "bmnw"
    );

    public static final Supplier<AttachmentType<Float>> RADIATION = ATTACHMENTS.register(
            "radiation", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );
    public static final Supplier<AttachmentType<Float>> QUEUED_RADIATION = ATTACHMENTS.register(
            "queued_radiation", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );
    public static final Supplier<AttachmentType<Float>> SOURCE_RADIOACTIVITY = ATTACHMENTS.register(
            "source_radioactivity", () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).build()
    );

    public static final Supplier<AttachmentType<Boolean>> SOURCED_RADIOACTIVITY_THIS_TICK = ATTACHMENTS.register(
            "sourced_radioactivity_this_tick", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    public static final Supplier<AttachmentType<Integer>> EXCAVATION_VEIN_DEPLETION = ATTACHMENTS.register(
            "excavation_vein_depletion", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );


    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
