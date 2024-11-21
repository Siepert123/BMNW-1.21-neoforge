package com.siepert.bmnw.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticleTypes {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "bmnw");

    public static final Supplier<SimpleParticleType> VOMIT = PARTICLE_TYPES.register(
            "vomit",
            () -> new SimpleParticleType(false)
    );

    public static final Supplier<SimpleParticleType> EVIL_FOG = PARTICLE_TYPES.register(
            "evil_fog",
            () -> new SimpleParticleType(false)
    );

    public static final Supplier<SimpleParticleType> FIRE_SMOKE = PARTICLE_TYPES.register(
            "fire_smoke",
            () -> new SimpleParticleType(false)
    );

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
