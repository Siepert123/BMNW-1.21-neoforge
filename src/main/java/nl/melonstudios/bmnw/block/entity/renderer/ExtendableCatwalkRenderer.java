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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.decoration.ExtendableCatwalkBlock;
import nl.melonstudios.bmnw.block.entity.ExtendableCatwalkBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Quaternionf;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ExtendableCatwalkRenderer implements BlockEntityRenderer<ExtendableCatwalkBlockEntity> {
    public ExtendableCatwalkRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(ExtendableCatwalkBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        Direction facing = blockEntity.getBlockState().getValue(ExtendableCatwalkBlock.FACING);
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-facing.toYRot())), 0.5F, 0.5F, 0.5F);
        RenderType renderType = RenderType.CUTOUT;
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        RandomSource rnd = RandomSource.create();
        float progress = Mth.clamp(blockEntity.getProgress(partialTick), 0, 1);
        float modProgress = progress * blockEntity.getCurrentExtensionParts();
        if (blockEntity.hasControls()) {
            poseStack.pushPose();
            poseStack.translate(0, 2, 0);
            poseStack.rotateAround(new Quaternionf().rotateX((float) -Math.toRadians(modProgress*360+blockEntity.getCrankOffset())), 0.5F, 0.5F, 0.5F);
            BakedModel crank = BMNWPartialModels.LARGE_WHEEL_CRANK.loadAndGet();
            this.renderBakedModel(consumer, poseStack.last(), crank, rnd, RenderType.SOLID, packedLight, packedOverlay);
            poseStack.popPose();
            poseStack.pushPose();
            BakedModel holder = BMNWPartialModels.EXTENDABLE_CATWALK_CONTROL_HOLDER.loadAndGet();
            poseStack.translate(0, 1, 0);
            this.renderBakedModel(consumer, poseStack.last(), holder, rnd, RenderType.SOLID, packedLight, packedOverlay);
            poseStack.popPose();
        }
        poseStack.translate(0.0F, 0.0F, modProgress);
        BakedModel part = BMNWPartialModels.EXTENDABLE_CATWALK_PART.loadAndGet();
        for (int i = 0; i < blockEntity.getCurrentExtensionParts(); i++) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, -i);
            this.renderBakedModel(consumer, poseStack.last(), part, rnd, renderType, packedLight, packedOverlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void renderBakedModel(VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, RenderType renderType, int packedLight, int packedOverlay) {
        for (Direction d : Library.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, renderType);
            for (BakedQuad quad : quads) {
                float b = Library.brightnessByDirection(quad.getDirection());
                consumer.putBulkData(last, quad, b, b, b, 1.0F, packedLight, packedOverlay);
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ExtendableCatwalkBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(ExtendableCatwalkBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public AABB getRenderBoundingBox(ExtendableCatwalkBlockEntity blockEntity) {
        return blockEntity.getRenderBB();
    }
}
