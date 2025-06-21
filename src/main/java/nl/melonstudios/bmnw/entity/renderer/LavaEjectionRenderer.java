package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class LavaEjectionRenderer extends EntityRenderer<LavaEjectionEntity> {
    public LavaEjectionRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static final Quaternionf quat = new Quaternionf();

    @Override
    public void render(LavaEjectionEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = p_entity.tickCount + partialTick;
        float rot = (float)Math.toRadians(age * 9);
        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, -0.5);
        quat.set(0, 0, 0, 1);
        poseStack.rotateAround(quat.rotateXYZ(rot, rot, rot), 0.5F, 0.5F, 0.5F);
        BakedModel model = p_entity.getLavaType().getModel().loadAndGet();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);
        this.renderModel(poseStack.last(), consumer, model);
        poseStack.popPose();
    }

    private void renderModel(PoseStack.Pose last, VertexConsumer consumer, BakedModel model) {
        RandomSource rnd = RandomSource.create();
        for (Direction direction : Library.DIRECTIONS_WITH_NULL) {
            for (BakedQuad quad : model.getQuads(null, direction, rnd, ModelData.EMPTY, RenderType.SOLID)) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, Library.PACKED_LIGHT, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(LavaEjectionEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
