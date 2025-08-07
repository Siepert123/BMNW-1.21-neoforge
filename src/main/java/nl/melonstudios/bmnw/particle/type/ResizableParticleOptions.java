package nl.melonstudios.bmnw.particle.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ResizableParticleOptions implements ParticleOptions {
    private final ParticleType<ResizableParticleOptions> type;
    private final float size;

    public static MapCodec<ResizableParticleOptions> codec(ParticleType<ResizableParticleOptions> type) {
        return Codec.FLOAT.xmap(p -> new ResizableParticleOptions(type, p), p -> p.size).fieldOf("size");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ResizableParticleOptions> streamCodec(ParticleType<ResizableParticleOptions> type) {
        return ByteBufCodecs.FLOAT.map(p -> new ResizableParticleOptions(type, p), p -> p.size);
    }

    public ResizableParticleOptions(ParticleType<ResizableParticleOptions> type, float size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public ParticleType<ResizableParticleOptions> getType() {
        return this.type;
    }

    public float getSize() {
        return this.size;
    }
}