package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.entity.missile.AbstractMissileEntity;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMissileRenderer<T extends AbstractMissileEntity> extends EntityRenderer<T> {
    protected AbstractMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        this.render(p_entity, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    protected abstract void render(T missile, float pt, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);
}
