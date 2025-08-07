package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FireTrailParticle extends TextureSheetParticle {
    private static final Random random = new Random();
    public FireTrailParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet
    ) {
        super(level, x, y, z, vX, vY + level.random.nextFloat() * 0.5f, vZ);
        this.quadSize = 2f + (level.random.nextFloat() * 0.5f);
        this.lifetime = 120;
        this.gravity = 0;
        this.hasPhysics = true;
        this.speedUpWhenYMotionIsBlocked = false;
        setColor(1, 0.5F, 0);
        this.spriteSet = spriteSet;
        this.vox = vX;
        this.voy = vY;
        this.voz = vZ;
    }

    private final double vox, voy, voz;

    private int coolingTicks = 80;
    private int dispersionTicks = 40;

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
        if (this.coolingTicks > 0) {
            float orangeness = this.coolingTicks / 80.0F;
            this.setColor(
                    Mth.lerp(orangeness, 0.3F, 1.0F),
                    Mth.lerp(orangeness, 0.3F, 0.5F),
                    Mth.lerp(orangeness, 0.3F, 0.0F)
            );
            this.coolingTicks--;
        } else if (this.dispersionTicks > 0) {
            this.setColor(0.3F, 0.3F, 0.3F);
            this.setAlpha(this.dispersionTicks / 40.0F);
            this.dispersionTicks--;
        }
        setSpriteFromAge(spriteSet);
        this.move(this.xd, this.yd, this.zd);
        this.xd = this.vox;
        this.yd = this.voy;
        this.zd = this.voz;
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
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}
