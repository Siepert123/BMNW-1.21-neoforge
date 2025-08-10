package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.particle.type.ResizableParticleOptions;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class FlashParticle extends TextureSheetParticle {
    private static final int EXTINGUISH_TICKS = 250;

    protected FlashParticle(ClientLevel level, double x, double y, double z, float size) {
        super(level, x, y, z);
        this.quadSize = size * 0.9F;
        this.lifetime = Mth.ceil(EXTINGUISH_TICKS * size * 0.01F);
        this.hasPhysics = false;
        this.gravity = 0;
        this.speedUpWhenYMotionIsBlocked = false;
    }

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

        if (this.age++ > this.lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<ResizableParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(ResizableParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FlashParticle particle = new FlashParticle(level, x, y, z, type.getSize());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
