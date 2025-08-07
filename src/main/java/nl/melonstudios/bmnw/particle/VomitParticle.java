package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class VomitParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public VomitParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        this(level, x, y, z, 0, 0, 0, spriteSet);
    }
    public VomitParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet) {
        super(level, x, y, z, vX, vY, vZ);
        this.spriteSet = spriteSet;
        this.gravity = 1;
        this.lifetime = 200;
        this.quadSize /= 2;
        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
