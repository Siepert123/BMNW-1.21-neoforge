package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.misc.math.IEase;
import nl.melonstudios.bmnw.particle.type.ResizableParticleOptions;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MushroomCloudParticle extends TextureSheetParticle {
    private static final IEase EASING_FUNC = Easing.OUT_QUART;
    private static final int LIFETIME = 1000;
    private static final int GROWTH_TICKS = 100;
    private static final int EXTINGUISH_TICKS = 250;
    private static final float ALPHA_EXCHANGE = 1.0F / (LIFETIME-EXTINGUISH_TICKS);
    protected MushroomCloudParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, float size
    ) {
        super(level, x, y, z, vX, vY, vZ);
        this.size = size;
        this.xd = x+vX;
        this.yd = y+vY;
        this.zd = z+vZ;
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.quadSize = (1.8f + (level.random.nextFloat() * 0.4f)) * this.size;
        this.lifetime = LIFETIME;
        this.hasPhysics = false;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = false;
        RandomSource rnd = RandomSource.create();
        this.darkness = rnd.nextFloat() * 0.2F + 0.3F;
        this.yellowness = rnd.nextFloat() * 0.2F + 0.8F;
        this.oRoll = this.roll = rnd.nextInt(-180, 180)*Mth.DEG_TO_RAD;
        this.setColor(1.0F, this.yellowness, 0.1F);
    }

    private final double startX, startY, startZ;
    private final float darkness;
    private final float yellowness;

    private final float size;

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age < GROWTH_TICKS) {
            float d = (float)this.age / GROWTH_TICKS;
            this.x = EASING_FUNC.clampedEasedLerp((float) this.startX, (float) this.xd, d);
            this.y = EASING_FUNC.clampedEasedLerp((float) this.startY, (float) this.yd, d);
            this.z = EASING_FUNC.clampedEasedLerp((float) this.startZ, (float) this.zd, d);
        }
        if (this.age < EXTINGUISH_TICKS) {
            float d = (float)this.age / EXTINGUISH_TICKS;
            this.setColor(
                    Mth.lerp(d, 1.0F, this.darkness),
                    Mth.lerp(d, this.yellowness, this.darkness),
                    Mth.lerp(d, 0.1F, this.darkness)
            );
        } else {
            this.alpha = Mth.clamp(this.alpha - ALPHA_EXCHANGE, 0, 1);
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.quadSize += (0.01F * this.size);
    }

    public static class Provider implements ParticleProvider<ResizableParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(ResizableParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MushroomCloudParticle particle = new MushroomCloudParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type.getSize());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
