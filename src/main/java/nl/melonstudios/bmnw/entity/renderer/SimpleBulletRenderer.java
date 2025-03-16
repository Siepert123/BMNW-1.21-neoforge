package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.entity.SimpleBulletEntity;

@OnlyIn(Dist.CLIENT)
public class SimpleBulletRenderer extends EntityRenderer<SimpleBulletEntity> {
    public SimpleBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SimpleBulletEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // noop
    }

    @Override
    public ResourceLocation getTextureLocation(SimpleBulletEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
