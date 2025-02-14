package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import nl.melonstudios.bmnw.entity.custom.AbstractBulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

public abstract class BulletRenderer<T extends AbstractBulletEntity> extends EntityRenderer<T> {
    protected BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(p_entity)));
        PoseStack.Pose pose = poseStack.last();
        this.vertex(pose, consumer, -7, -2, -2, 0.0F, 1, -1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, -2, 2, 1, 1, -1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, 2, 2, 1, 1, -1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, 2, -2, 0.0F, 1, -1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, 2, -2, 0.0F, 1, 1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, 2, 2, 1, 1, 1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, -2, 2, 1, 1, 1, 0, 0, packedLight);
        this.vertex(pose, consumer, -7, -2, -2, 0.0F, 1, 1, 0, 0, packedLight);
        poseStack.popPose();
    }

    public void vertex(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int x,
            int y,
            int z,
            float u,
            float v,
            int normalX,
            int normalY,
            int normalZ,
            int packedLight
    ) {
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, (float)normalX, (float)normalZ, (float)normalY);
    }
}
