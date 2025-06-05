package nl.melonstudios.bmnw.interfaces;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ISpriteSetter {
    void setSpriteTexture(TextureAtlasSprite sprite);

    static void setSpriteFromModel(ISpriteSetter particle, BakedModel model) {
        particle.setSpriteTexture(model.getParticleIcon(ModelData.EMPTY));
    }
}
