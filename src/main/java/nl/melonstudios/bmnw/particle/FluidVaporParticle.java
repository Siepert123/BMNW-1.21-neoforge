package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class FluidVaporParticle extends TextureSheetParticle {
    protected FluidVaporParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        float r = (float) xSpeed;
        float g = (float) ySpeed;
        float b = (float) zSpeed;

        this.quadSize = 1.0F;
        this.lifetime = 200;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = false;

        this.setColor(r, g, b);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.move(Math.abs(this.random.nextGaussian()) * 0.1, 0.2, Math.abs(this.random.nextGaussian()) * 0.1);

        if (this.age++ > this.lifetime) {
            this.remove();
        }

        this.quadSize += 0.025F * (float)Math.abs(this.random.nextGaussian());
        this.alpha = Math.max(0, this.alpha - 0.005F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FluidVaporParticle particle = new FluidVaporParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
