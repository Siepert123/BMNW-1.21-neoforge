package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FireTrailParticle extends TextureSheetParticle {
    protected FireTrailParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet
    ) {
        super(level, x, y, z, vX, vY + level.random.nextFloat() * 0.5f, vZ);
        this.quadSize = 2f + (level.random.nextFloat() * 0.5f);
        this.lifetime = 200;
        this.gravity = 0;
        this.hasPhysics = false;
        this.speedUpWhenYMotionIsBlocked = false;
        setColor(1, 0, 0);
        this.spriteSet = spriteSet;
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
        setSpriteFromAge(spriteSet);
        float baseColor = Math.min(1.5f - (float) age / lifetime, 1);
        setColor(baseColor, baseColor / 2, 0);
    }

    private final SpriteSet spriteSet;

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FireTrailParticle particle = new FireTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }
}
