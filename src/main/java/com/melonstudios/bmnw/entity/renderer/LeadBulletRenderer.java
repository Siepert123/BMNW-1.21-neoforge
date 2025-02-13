package com.melonstudios.bmnw.entity.renderer;

import com.melonstudios.bmnw.BMNW;
import com.melonstudios.bmnw.entity.custom.LeadBulletEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LeadBulletRenderer extends BulletRenderer<LeadBulletEntity> {
    public LeadBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LeadBulletEntity entity) {
        return BMNW.namespace("textures/item/playstation.png"); //TODO: replace with Actual Texture
    }
}
