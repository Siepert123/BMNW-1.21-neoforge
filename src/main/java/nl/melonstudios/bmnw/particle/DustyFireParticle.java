package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class DustyFireParticle extends TextureSheetParticle {
    protected DustyFireParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet
    ) {
        super(level, x, y, z, vX, vY + level.random.nextFloat() * 0.5f, vZ);
        this.quadSize = 0.1f + (level.random.nextFloat() * 0.1f);
        this.lifetime = 20 + level.random.nextInt(20);
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
        setColor(1, (float) age / lifetime, 0);
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
            DustyFireParticle particle = new DustyFireParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }
}
