package nl.melonstudios.bmnw.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class LargeMissileSmokeParticle extends TextureSheetParticle {
    protected LargeMissileSmokeParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ
    ) {
        super(level, x, y, z, vX, vY, vZ);
        this.xd = vX;
        this.yd = vY;
        this.zd = vZ;
        this.quadSize = 1.8f + (level.random.nextFloat() * 0.4f);
        this.lifetime = 200;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = false;
        RandomSource rnd = RandomSource.create();
        this.darkness = rnd.nextFloat() * 0.2F;
        this.orangeness = rnd.nextFloat() * 0.2F + 0.1F;
        this.oRoll = this.roll = rnd.nextInt(-180, 180)*Mth.DEG_TO_RAD;
        this.setColor(1.0F, this.orangeness, 0.1F);
    }

    private final float darkness;
    private final float orangeness;

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.move(this.xd, this.yd, this.zd);
        this.xd = Mth.clamp(this.xd - 0.05, 0, Double.MAX_VALUE);
        this.yd = Mth.clamp(this.yd - 0.05, 0, Double.MAX_VALUE);
        this.zd = Mth.clamp(this.zd - 0.05, 0, Double.MAX_VALUE);
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.quadSize += 0.05F;
        if (this.age < 100) {
            this.setColor(Mth.lerp((this.age * 0.01F), 1.0F, this.darkness),
                    Mth.lerp(this.age*0.01F, this.orangeness, this.darkness),
                    Mth.lerp(this.age*0.01F, 0.1F, this.darkness));
        } else {
            this.alpha = Mth.clamp(this.alpha - 0.01F, 0, 1);
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        if (this.age < 100) {
            return LightTexture.FULL_BRIGHT;
        }
        return super.getLightColor(partialTick);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        super.render(buffer, renderInfo, partialTicks);
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
