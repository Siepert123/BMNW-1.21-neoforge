package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.entity.LargeShredderBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class LargeShredderRenderer implements BlockEntityRenderer<LargeShredderBlockEntity> {
    private static final float HATCH_RADIANS = 1.0F;
    private static final float BLADES_SPEED_MODIFIER = 10.0F;

    public LargeShredderRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(LargeShredderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(blockEntity.getFacing().toYRot())), 0.5F, 0.5F, 0.5F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.CUTOUT);

        poseStack.pushPose();
        float thing = System.currentTimeMillis() % 60000 / 1000.0F;
        poseStack.rotateAround(new Quaternionf().rotateX((-thing)*BLADES_SPEED_MODIFIER),
                0.5F, 0.25F, 0.5F);
        this.render(consumer, poseStack.last(), BMNWPartialModels.LARGE_SHREDDER_BLADES.loadAndGet(), packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        if (blockEntity.running == blockEntity.oldRunning) {
            if (blockEntity.running) poseStack.rotateAround(new Quaternionf().rotateX(HATCH_RADIANS), 0.5F, 0.25F, 0.5F);
            this.render(consumer, poseStack.last(), BMNWPartialModels.LARGE_SHREDDER_HATCH.loadAndGet(), packedLight, packedOverlay);
        } else {
            float t = (blockEntity.oldRunning ? 1.0F - partialTick : partialTick) / HATCH_RADIANS;
            poseStack.rotateAround(new Quaternionf().rotateX(t), 0.5F, 0.25F, 0.5F);
            this.render(consumer, poseStack.last(), BMNWPartialModels.LARGE_SHREDDER_HATCH.loadAndGet(), packedLight, packedOverlay);
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    private void render(VertexConsumer consumer, PoseStack.Pose last, BakedModel model, int light, int overlay) {
        RandomSource rnd = RandomSource.create();
        for (Direction d : Direction.values()) {
            for (BakedQuad quad : model.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.CUTOUT)) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
            }
        }
        for (BakedQuad quad : model.getQuads(null, null, rnd, ModelData.EMPTY, RenderType.CUTOUT)) {
            consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
        }
    }

    @Override
    public AABB getRenderBoundingBox(LargeShredderBlockEntity blockEntity) {
        if (blockEntity.renderBB == null) {
            blockEntity.renderBB = new AABB(blockEntity.getBlockPos()).inflate(1);
        }
        return blockEntity.renderBB;
    }

    @Override
    public int getViewDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldRender(LargeShredderBlockEntity blockEntity, Vec3 cameraPos) {
        return !blockEntity.multiblockSlave;
    }
}
