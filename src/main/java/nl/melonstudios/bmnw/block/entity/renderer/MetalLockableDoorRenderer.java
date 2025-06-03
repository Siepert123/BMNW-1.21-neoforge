package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.doors.MetalLockableDoorBlock;
import nl.melonstudios.bmnw.block.entity.MetalLockableDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class MetalLockableDoorRenderer implements BlockEntityRenderer<MetalLockableDoorBlockEntity> {
    public MetalLockableDoorRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MetalLockableDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(MetalLockableDoorBlock.UPPER_HALF)) return;
        RandomSource rnd = RandomSource.create();
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-state.getValue(MetalLockableDoorBlock.FACING).toYRot())),
                0.5F, 0, 0.5F);

        BakedModel door = BMNWPartialModels.METAL_LOCKABLE_DOOR.loadAndGet();
        BakedModel handle = BMNWPartialModels.METAL_DOOR_HANDLE.loadAndGet();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);

        if (state.getValue(MetalLockableDoorBlock.MIRRORED)) {
            poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(90 * blockEntity.getDoor(partialTick))),
                    1-0.0625F, 0.0F, 0.0625F);
            this.renderBakedModel(poseStack, consumer, poseStack.last(), door, rnd, RenderType.SOLID, packedLight, packedOverlay);
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.rotateAround(new Quaternionf().rotateZ((float) Math.toRadians(90 * (1.0F-blockEntity.getHandle(partialTick)))),
                    0.5F, 0.5F, 0.5F);
            this.renderBakedModel(poseStack, consumer, poseStack.last(), handle, rnd, RenderType.SOLID, packedLight, packedOverlay);
        } else {
            poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-90 * blockEntity.getDoor(partialTick))),
                    0.0625F, 0.0F, 0.0625F);
            this.renderBakedModel(poseStack, consumer, poseStack.last(), door, rnd, RenderType.SOLID, packedLight, packedOverlay);
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.rotateAround(new Quaternionf().rotateZ((float) Math.toRadians(90 * blockEntity.getHandle(partialTick))),
                    0.5F, 0.5F, 0.5F);
            this.renderBakedModel(poseStack, consumer, poseStack.last(), handle, rnd, RenderType.SOLID, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    private void renderBakedModel(PoseStack poseStack, VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
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
    public AABB getRenderBoundingBox(MetalLockableDoorBlockEntity blockEntity) {
        return blockEntity.getCachedBB();
    }

    @Override
    public boolean shouldRender(MetalLockableDoorBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }
}
