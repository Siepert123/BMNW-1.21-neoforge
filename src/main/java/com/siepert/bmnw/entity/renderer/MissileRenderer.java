package com.siepert.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.siepert.bmnw.entity.custom.MissileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class MissileRenderer extends EntityRenderer<MissileEntity> {
    public MissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MissileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        Vector3f vector3f = new Vector3f(-0.5f, 0, -0.5f);
        Vector3f vector3f1 = new Vector3f(0.5f, 0, -0.5f);
        Vector3f vector3f2 = new Vector3f(0.5f, 0, 0.5f);
        Vector3f vector3f3 = new Vector3f(-0.5f, 0, 0.5f);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.CUTOUT);

        buffer.addVertex(vector3f).setColor(0xffffff).setUv(0, 0).setUv1(0, 0).setUv2(1, 1).setNormal(vector3f.normalize().x, vector3f3.normalize().y, vector3f3.normalize().z);
        buffer.addVertex(vector3f1).setColor(0xffffff).setUv(0, 0).setUv1(0, 0).setUv2(1, 1).setNormal(vector3f1.normalize().x, vector3f3.normalize().y, vector3f3.normalize().z);
        buffer.addVertex(vector3f2).setColor(0xffffff).setUv(0, 0).setUv1(0, 0).setUv2(1, 1).setNormal(vector3f2.normalize().x, vector3f3.normalize().y, vector3f3.normalize().z);
        buffer.addVertex(vector3f3).setColor(0xffffff).setUv(0, 0).setUv1(0, 0).setUv2(1, 1).setNormal(vector3f3.normalize().x, vector3f3.normalize().y, vector3f3.normalize().z);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MissileEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(MissileEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
