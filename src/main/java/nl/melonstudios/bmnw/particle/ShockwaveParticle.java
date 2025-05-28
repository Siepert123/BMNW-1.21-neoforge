package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.interfaces.ISpriteSetter;

@OnlyIn(Dist.CLIENT)
public class ShockwaveParticle extends TextureSheetParticle {
    public ShockwaveParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.setSize(4.0F, 4.0F);
        ISpriteSetter.setSpriteFromModel(this::setSprite, BMNWPartialModels.PARTICLE_GENERIC[7].loadAndGet());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float p = (float)this.age / this.lifetime;
            this.setAlpha(1.0F-p);
        }
    }
}
