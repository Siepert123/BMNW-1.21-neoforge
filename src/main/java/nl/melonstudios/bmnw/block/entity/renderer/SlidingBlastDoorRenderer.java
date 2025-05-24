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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.doors.SlidingBlastDoorBlock;
import nl.melonstudios.bmnw.block.entity.SealedHatchBlockEntity;
import nl.melonstudios.bmnw.block.entity.SlidingBlastDoorBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.SI;
import nl.melonstudios.bmnw.misc.math.Easing;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class SlidingBlastDoorRenderer implements BlockEntityRenderer<SlidingBlastDoorBlockEntity> {
    public SlidingBlastDoorRenderer(BlockEntityRendererProvider.Context context) {

    }

    private static float pos(int px32) {
        return px32 / 32.0F;
    }

    private static final Vector2f[] SCREW_OFFSETS = {
            new Vector2f(pos(-2), pos(-2)),
            new Vector2f(pos(16), pos(-2)),
            new Vector2f(pos(34), pos(-2)),
            new Vector2f(pos(-2), pos(15)),
            new Vector2f(pos(-2), pos(32)),
            new Vector2f(pos(-2), pos(49)),
            new Vector2f(pos(-2), pos(66)),
            new Vector2f(pos(34), pos(15)),
            new Vector2f(pos(34), pos(32)),
            new Vector2f(pos(34), pos(49)),
            new Vector2f(pos(34), pos(66)),
            new Vector2f(pos(16), pos(66)),
    };

    @Override
    public void render(SlidingBlastDoorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(SlidingBlastDoorBlock.UPPER_HALF)) return; //Prevent upper half rendering
        RandomSource rnd = RandomSource.create();
        poseStack.pushPose();

        //Rotate to match block facing
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-state.getValue(
                BlockStateProperties.HORIZONTAL_FACING).toYRot())),
                0.5F, 0, 0.5F);

        //Obtain the vertex consumer
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);

        //Get partial models for rendering
        BakedModel sliding_door = BMNWPartialModels.SLIDING_BLAST_DOOR.loadAndGet();
        BakedModel screw = BMNWPartialModels.SCREW.loadAndGet();
        poseStack.pushPose();
        poseStack.translate(Easing.OUT_QUAD.ease(blockEntity.getSlide(partialTick)) * 0.99F, 0, 0);
        PoseStack.Pose pose = poseStack.last();
        for (Direction d : SI.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = sliding_door.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.SOLID);
            for (BakedQuad quad : quads) {
                consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay);
            }
        }
        poseStack.popPose();
        poseStack.translate(-0.5F, -0.5F, -0.9375f - blockEntity.getScrewExpansion(partialTick));
        for (Vector2f vec : SCREW_OFFSETS) {
            poseStack.pushPose();
            poseStack.translate(vec.x, vec.y, 0.0F);
            poseStack.rotateAround(new Quaternionf().rotateZ((float) Math.toRadians(blockEntity.getScrewRot(partialTick) * 360)),
                    0.5F, 0.5F, 0);
            this.renderBakedModel(poseStack, consumer, poseStack.last(), screw, rnd, RenderType.SOLID, packedLight, packedOverlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void renderBakedModel(PoseStack poseStack, VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, RenderType renderType, int packedLight, int packedOverlay) {
        for (Direction d : SI.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, renderType);
            for (BakedQuad quad : quads) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 10F, 1.0F, packedLight, packedOverlay);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(SlidingBlastDoorBlockEntity blockEntity) {
        return blockEntity.getCachedBB();
    }

    @Override
    public boolean shouldRender(SlidingBlastDoorBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }
}
