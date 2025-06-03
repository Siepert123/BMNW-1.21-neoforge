package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DustTrailParticle extends TextureSheetParticle {
    protected DustTrailParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.xd = this.yd = this.zd = 0;
        this.gravity = 0;
        this.hasPhysics = false;
        this.spriteSet = spriteSet;
        this.setColor(0.5F, 0.5F, 0.5F);
        this.quadSize = 2.5F;
    }

    private static final int lifetime = 40;

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ > lifetime) {
            this.remove();
        } else {
            this.setSprite(this.spriteSet.get(this.age, lifetime));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    private final SpriteSet spriteSet;

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DustTrailParticle particle = new DustTrailParticle(level, x, y, z, this.spriteSet);
            particle.setSprite(this.spriteSet.get(0, lifetime));
            return particle;
        }
    }
}
