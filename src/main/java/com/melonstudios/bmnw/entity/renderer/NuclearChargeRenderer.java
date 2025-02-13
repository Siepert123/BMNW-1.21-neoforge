package com.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.melonstudios.bmnw.entity.custom.NuclearChargeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class NuclearChargeRenderer extends EntityRenderer<NuclearChargeEntity> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EYES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
    private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
    private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public NuclearChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NuclearChargeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float f = (partialTick) / 200f;
        poseStack.pushPose();
        poseStack.translate(0, 0, 0);
        renderRays(poseStack, f, buffer.getBuffer(RenderType.dragonRays()));
        renderRays(poseStack, f, buffer.getBuffer(RenderType.dragonRaysDepth()));
        poseStack.popPose();
    }
    protected static void renderRays(PoseStack poseStack, float dragonDeathCompletion, VertexConsumer buffer) {
        renderRays(poseStack, dragonDeathCompletion, buffer, 1);
    }
    protected static void renderRays(PoseStack poseStack, float dragonDeathCompletion, VertexConsumer buffer, float sizeMultiplier) {
        poseStack.pushPose();
        float f = Math.min(dragonDeathCompletion > 0.8F ? (dragonDeathCompletion - 0.8F) / 0.2F : 0.0F, 1.0F);
        int i = FastColor.ARGB32.colorFromFloat(1.0F - f, 1.0F, 1.0F, 1.0F);
        RandomSource randomsource = RandomSource.create("nuclear_charge".hashCode());
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f1 = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int k = 60;

        for (int l = 0; l < k; l++) {
            quaternionf.rotationXYZ(
                            randomsource.nextFloat() * (float) (Math.PI * 2),
                            randomsource.nextFloat() * (float) (Math.PI * 2),
                            randomsource.nextFloat() * (float) (Math.PI * 2)
                    )
                    .rotateXYZ(
                            randomsource.nextFloat() * (float) (Math.PI * 2),
                            randomsource.nextFloat() * (float) (Math.PI * 2),
                            randomsource.nextFloat() * (float) (Math.PI * 2) + dragonDeathCompletion * (float) (Math.PI / 2)
                    );
            poseStack.mulPose(quaternionf);
            float f1 = randomsource.nextFloat() * 20.0F + 5.0F + f * 10.0F;
            float f2 = randomsource.nextFloat() * 2.0F + 1.0F + f * 2.0F;
            vector3f1.set(-HALF_SQRT_3 * f2*sizeMultiplier, f1*sizeMultiplier, -0.5F * f2*sizeMultiplier);
            vector3f2.set(HALF_SQRT_3 * f2*sizeMultiplier, f1*sizeMultiplier, -0.5F * f2*sizeMultiplier);
            vector3f3.set(0.0F, f1*sizeMultiplier, f2*sizeMultiplier);
            PoseStack.Pose posestack$pose = poseStack.last();
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f1).setColor(0xff00ff);
            buffer.addVertex(posestack$pose, vector3f2).setColor(0xff00ff);
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f2).setColor(0xff00ff);
            buffer.addVertex(posestack$pose, vector3f3).setColor(0xff00ff);
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f3).setColor(0xff00ff);
            buffer.addVertex(posestack$pose, vector3f1).setColor(0xff00ff);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(NuclearChargeEntity entity) {
        return DRAGON_LOCATION;
    }

    @Override
    public boolean shouldRender(NuclearChargeEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
