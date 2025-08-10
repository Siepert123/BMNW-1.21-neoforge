package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.misc.math.IEase;
import nl.melonstudios.bmnw.particle.type.ResizableParticleOptions;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MushroomSmokeParticle extends TextureSheetParticle {
    private static final IEase EASING_FUNC = Easing.OUT_QUART;
    private static final int LIFETIME = 1000;
    private static final int GROWTH_TICKS = 100;
    private static final int EXTINGUISH_TICKS = 250;
    private static final float ALPHA_EXCHANGE = 1.0F / (LIFETIME-EXTINGUISH_TICKS);

    private final int myGrowthTicks, myExtinguishTicks;
    private final float myAlphaExchange;

    private final SpriteSet spriteSet;

    protected MushroomSmokeParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, float size, SpriteSet spriteSet) {
        super(level, x, y, z, vX, vY, vZ);
        this.size = size;
        this.xd = x+vX;
        this.yd = y+vY;
        this.zd = z+vZ;
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.quadSize = (0.9f + (level.random.nextFloat() * 0.2f)) * this.size;
        this.lifetime = Mth.ceil(LIFETIME * this.size);
        this.myGrowthTicks = Mth.ceil(GROWTH_TICKS * this.size);
        this.myExtinguishTicks = Mth.ceil(EXTINGUISH_TICKS * this.size);
        this.myAlphaExchange = 1.0F / (this.lifetime-this.myExtinguishTicks);
        this.hasPhysics = false;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = false;
        RandomSource rnd = RandomSource.create();
        this.darkness = rnd.nextFloat() * 0.2F;
        this.oRoll = this.roll = rnd.nextInt(-180, 180)* Mth.DEG_TO_RAD;
        this.setColor(0.8F, 0.8F, 0.8F);
        this.spriteSet = spriteSet;
    }

    private final double startX, startY, startZ;
    private final float darkness;

    private final float size;

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age < this.myGrowthTicks) {
            float d = (float)this.age / this.myGrowthTicks;
            this.x = EASING_FUNC.clampedEasedLerp((float) this.startX, (float) this.xd, d);
            this.y = EASING_FUNC.clampedEasedLerp((float) this.startY, (float) this.yd, d);
            this.z = EASING_FUNC.clampedEasedLerp((float) this.startZ, (float) this.zd, d);
        }
        if (this.age < this.myExtinguishTicks) {
            float d = (float)this.age / this.myExtinguishTicks;
            this.setColor(
                    Mth.lerp(d, 0.8F, this.darkness),
                    Mth.lerp(d, 0.8F, this.darkness),
                    Mth.lerp(d, 0.8F, this.darkness)
            );
        }

        this.quadSize += (0.005F * this.size);

        this.setSpriteFromAge(this.spriteSet);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void setSpriteFromAge(SpriteSet sprite) {
        if (!this.removed) {
            this.setSprite(sprite.get(this.getFakeAge(), 6));
        }
    }

    private int getFakeAge() {
        if (this.age < this.myExtinguishTicks) return 0;
        int diff = this.lifetime - this.myExtinguishTicks;
        int thing = this.age - this.myExtinguishTicks;
        float delta = (float) thing / diff;
        return Mth.floor(delta * 6);
    }

    public static class Provider implements ParticleProvider<ResizableParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(ResizableParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MushroomSmokeParticle particle = new MushroomSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type.getSize(), this.spriteSet);
            particle.setSpriteFromAge(this.spriteSet);
            return particle;
        }
    }
}
