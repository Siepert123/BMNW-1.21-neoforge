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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.doors.MetalSlidingDoorBlock;
import nl.melonstudios.bmnw.block.entity.MetalSlidingDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.init.BMNWStateProperties;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Quaternionf;

import java.util.List;

public class MetalSlidingDoorRenderer implements BlockEntityRenderer<MetalSlidingDoorBlockEntity> {
    public MetalSlidingDoorRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MetalSlidingDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-state.getValue(MetalSlidingDoorBlock.FACING).toYRot())),
                0.5F, 0.0F, 0.5F);

        float door = blockEntity.getDoor(partialTick) * (state.getValue(BMNWStateProperties.MIRRORED) ? -1 : 1);
        poseStack.translate(-door, 0, 0);

        BakedModel model = BMNWPartialModels.METAL_SLIDING_DOOR.loadAndGet();
        this.renderBakedModel(bufferSource.getBuffer(RenderType.SOLID), poseStack.last(), model,
                RandomSource.create(), packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderBakedModel(VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, int packedLight, int packedOverlay) {
        for (Direction d : Library.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.SOLID);
            for (BakedQuad quad : quads) {
                float b = Library.brightnessByDirection(quad.getDirection());
                consumer.putBulkData(last, quad, b, b, b, 1.0F, packedLight, packedOverlay);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(MetalSlidingDoorBlockEntity blockEntity) {
        return blockEntity.getCachedBB();
    }

    @Override
    public boolean shouldRender(MetalSlidingDoorBlockEntity blockEntity, Vec3 cameraPos) {
        return !(blockEntity.canSwitchState() && blockEntity.open);
    }
}
