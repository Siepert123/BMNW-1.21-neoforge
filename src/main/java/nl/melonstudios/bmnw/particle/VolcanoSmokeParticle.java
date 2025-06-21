package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class VolcanoSmokeParticle extends TextureSheetParticle {
    protected VolcanoSmokeParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.quadSize = 0;
        this.gravity = 0;
        this.hasPhysics = false;
        this.speedUpWhenYMotionIsBlocked = false;
        float brightness = RandomSource.create().nextFloat() * 0.25F;
        this.setColor(brightness, brightness, brightness);
        this.spriteSet = spriteSet;
        this.lifetime = 40;
    }

    private final SpriteSet spriteSet;
    private int rise = 16;
    private float lastQuadSize = 0;
    private boolean firstTick = true;
    @Override
    public float getQuadSize(float scaleFactor) {
        return Mth.lerp(scaleFactor, this.lastQuadSize, this.quadSize);
    }

    @Override
    public void tick() {
        this.lastQuadSize = this.quadSize;
        this.quadSize = Mth.clamp(this.age * 5, 0, 32);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ > this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.spriteSet);
        if (!this.firstTick && this.rise > 0) {
            this.y += this.rise;
            this.rise--;
        }
        this.firstTick = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VolcanoSmokeParticle particle = new VolcanoSmokeParticle(level, x, y, z, this.spriteSet);
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}
