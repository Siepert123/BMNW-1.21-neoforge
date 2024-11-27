package com.siepert.bmnw.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class LargeMissileSmokeParticle extends TextureSheetParticle {
    protected LargeMissileSmokeParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ
    ) {
        super(level, x, y, z, vX, vY, vZ);
        this.quadSize = 1.8f + (level.random.nextFloat() * 0.4f);
        this.lifetime = 1000;
        this.gravity = 0;
        this.hasPhysics = false;
        this.speedUpWhenYMotionIsBlocked = false;
        setAlpha(0.9f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LargeMissileSmokeParticle particle = new LargeMissileSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }
}
