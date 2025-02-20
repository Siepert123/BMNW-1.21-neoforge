package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MushroomCloudParticle extends TextureSheetParticle {
    protected MushroomCloudParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet) {
        super(level, x, y, z, vX, vY, vZ);
        this.lifetime = 1000;
        this.gravity = 0;
        this.friction = 0.8f;
        this.quadSize = 0.1f;
        this.setColor(1, 0.5f, 0);
    }

    private final float maxSize = 1;

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xffffff;
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize = Math.min(this.quadSize + 0.1f, maxSize);
        float base = Math.min(1.5f - (float) this.age / this.lifetime, 1);
        this.rCol = base;
        this.gCol = base / 2;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MushroomCloudParticle particle = new MushroomCloudParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }
}
