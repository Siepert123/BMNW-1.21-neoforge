package nl.melonstudios.bmnw.weapon.explosion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionHelperRenderer extends EntityRenderer<ExplosionHelperEntity> {
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public ExplosionHelperRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ExplosionHelperEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        NukeType type = p_entity.nukeType;

        if (type != null && type.lightBeamMultiplier() > 0) {
            poseStack.pushPose();
            renderRays(poseStack, p_entity.age + partialTick, bufferSource.getBuffer(RenderType.DRAGON_RAYS), type);
            renderRays(poseStack, p_entity.age + partialTick, bufferSource.getBuffer(RenderType.DRAGON_RAYS_DEPTH), type);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(ExplosionHelperEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    private static void renderRays(PoseStack poseStack, float dragonDeathCompletion, VertexConsumer buffer, NukeType type) {
        poseStack.pushPose();
        float f = Math.min(dragonDeathCompletion > 0.8F ? (dragonDeathCompletion - 0.8F) / 0.2F : 0.0F, 1.0F);
        int i = FastColor.ARGB32.colorFromFloat(1.0F - f, 1.0F, 1.0F, 1.0F);
        int j = 16711935;
        RandomSource randomsource = RandomSource.create(432L);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f1 = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int k = Mth.floor((dragonDeathCompletion + dragonDeathCompletion * dragonDeathCompletion) / 2.0F * 60.0F);

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
            vector3f1.set(-HALF_SQRT_3 * f2, f1, -0.5F * f2);
            vector3f2.set(HALF_SQRT_3 * f2, f1, -0.5F * f2);
            vector3f3.set(0.0F, f1, f2);
            PoseStack.Pose posestack$pose = poseStack.last();
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f1).setColor(16711935);
            buffer.addVertex(posestack$pose, vector3f2).setColor(16711935);
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f2).setColor(16711935);
            buffer.addVertex(posestack$pose, vector3f3).setColor(16711935);
            buffer.addVertex(posestack$pose, vector3f).setColor(i);
            buffer.addVertex(posestack$pose, vector3f3).setColor(16711935);
            buffer.addVertex(posestack$pose, vector3f1).setColor(16711935);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosionHelperEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
