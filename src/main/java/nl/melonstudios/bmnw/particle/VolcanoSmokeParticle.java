package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.misc.math.Easing;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class VolcanoSmokeParticle extends TextureSheetParticle {
    protected VolcanoSmokeParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        RandomSource rnd = RandomSource.create();
        this.quadSize = 0;
        this.gravity = 0;
        this.hasPhysics = false;
        this.speedUpWhenYMotionIsBlocked = false;
        float brightness = rnd.nextFloat() * 0.25F;
        this.setColor(brightness, brightness, brightness);
        this.spriteSet = spriteSet;
        this.lifetime = 100;
        this.startX = (float)this.x;
        this.startZ = (float)this.z;
        this.xOffset = rnd.nextFloat() * 16 - 8;
        this.zOffset = rnd.nextFloat() * 16 - 8;
        this.maxQuadSize = 30 + rnd.nextFloat() * 4;
    }

    private final float startX, startZ;
    private final float xOffset, zOffset;
    private final SpriteSet spriteSet;
    private int rise = 16;
    private float lastQuadSize = 0;
    private boolean firstTick = true;
    private final float maxQuadSize;
    @Override
    public float getQuadSize(float partialTick) {
        return Easing.OUT_QUAD.clampedEasedLerp(0, this.maxQuadSize, Mth.clamp(this.ageToLifetime(partialTick) * 4, 0, 1));
    }

    private float ageToLifetime(float pt) {
        return (this.age+pt)/this.lifetime;
    }

    @Override
    public void tick() {
        this.lastQuadSize = this.quadSize;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ > this.lifetime) {
            this.remove();
            return;
        }
        this.quadSize = Mth.clamp(this.age * 3, 0, this.maxQuadSize);
        this.setSpriteFromAge(this.spriteSet);
        this.x = Easing.OUT_SINE.clampedEasedLerp(this.startX, this.startX+this.xOffset, (float)this.age / this.lifetime);
        this.z = Easing.OUT_SINE.clampedEasedLerp(this.startZ, this.startZ+this.zOffset, (float)this.age / this.lifetime);
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
