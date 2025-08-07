package nl.melonstudios.bmnw.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.particle.type.ResizableParticleOptions;

import java.util.function.Supplier;

public class BMNWParticleTypes {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "bmnw");

    public static final Supplier<SimpleParticleType> VOMIT = PARTICLE_TYPES.register(
            "vomit",
            () -> new SimpleParticleType(true)
    );

    public static final Supplier<SimpleParticleType> EVIL_FOG = PARTICLE_TYPES.register(
            "evil_fog",
            () -> new SimpleParticleType(true)
    );

    public static final Supplier<SimpleParticleType> FIRE_SMOKE = PARTICLE_TYPES.register(
            "fire_smoke",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> SMOKE_HD = PARTICLE_TYPES.register(
            "smoke_hd",
            () -> new SimpleParticleType(false)
    );
    public static final Supplier<SimpleParticleType> SHOCKWAVE = PARTICLE_TYPES.register(
            "shockwave",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> LARGE_MISSILE_SMOKE = PARTICLE_TYPES.register(
            "large_missile_smoke",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> DUSTY_FIRE = PARTICLE_TYPES.register(
            "dusty_fire",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> FIRE_TRAIL = PARTICLE_TYPES.register(
            "fire_trail",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<ParticleType<ResizableParticleOptions>> MUSHROOM_CLOUD = PARTICLE_TYPES.register(
            "mushroom_cloud",
            () -> new ParticleType<ResizableParticleOptions>(true) {
                @Override
                public MapCodec<ResizableParticleOptions> codec() {
                    return ResizableParticleOptions.codec(this);
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, ResizableParticleOptions> streamCodec() {
                    return ResizableParticleOptions.streamCodec(this);
                }
            }
    );
    public static final Supplier<ParticleType<ResizableParticleOptions>> MUSHROOM_SMOKE = PARTICLE_TYPES.register(
            "mushroom_smoke",
            () -> new ParticleType<ResizableParticleOptions>(true) {
                @Override
                public MapCodec<ResizableParticleOptions> codec() {
                    return ResizableParticleOptions.codec(this);
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, ResizableParticleOptions> streamCodec() {
                    return ResizableParticleOptions.streamCodec(this);
                }
            }
    );
    public static final Supplier<SimpleParticleType> DUST_TRAIL = PARTICLE_TYPES.register(
            "dust_trail",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> VOLCANO_SMOKE = PARTICLE_TYPES.register(
            "volcano_smoke",
            () -> new SimpleParticleType(true)
    );
    public static final Supplier<SimpleParticleType> FLUID_VAPOR = PARTICLE_TYPES.register(
            "fluid_vapor",
            () -> new SimpleParticleType(true)
    );


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
