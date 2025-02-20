package nl.melonstudios.bmnw.entity.renderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.entity.LeadBulletEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class LeadBulletRenderer extends BulletRenderer<LeadBulletEntity> {
    public LeadBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LeadBulletEntity entity) {
        return BMNW.namespace("textures/item/playstation.png"); //TODO: replace with Actual Texture
    }
}
