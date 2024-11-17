package com.siepert.bmnw.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FireSmokeParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;
    public FireSmokeParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new FireSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
    }
}
