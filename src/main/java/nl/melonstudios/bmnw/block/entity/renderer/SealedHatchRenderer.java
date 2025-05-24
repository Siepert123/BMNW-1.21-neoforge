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
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.doors.SealedHatchBlock;
import nl.melonstudios.bmnw.block.entity.SealedHatchBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.SI;
import nl.melonstudios.bmnw.misc.math.Easing;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class SealedHatchRenderer implements BlockEntityRenderer<SealedHatchBlockEntity> {
    public SealedHatchRenderer(BlockEntityRendererProvider.Context context) {

    }

    public static final float DESTINATION_ANGLE = 140.0F;

    @Override
    public void render(SealedHatchBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        RandomSource rnd = RandomSource.create();

        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-state.getValue(SealedHatchBlock.FACING).toYRot())),
                0.5F, 0, 0.5F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);

        BakedModel sealed_hatch = BMNWPartialModels.SEALED_HATCH.loadAndGet();
        BakedModel valve_handle_double = BMNWPartialModels.VALVE_HANDLE_DOUBLE.loadAndGet();

        poseStack.rotateAround(new Quaternionf().rotateX((float)
                -Math.toRadians(Easing.OUT_CUBIC.ease(blockEntity.getOpen(partialTick)) * DESTINATION_ANGLE)
        ), 0, 0, 0);

        this.renderBakedModel(poseStack, consumer, poseStack.last(), sealed_hatch, rnd, RenderType.SOLID, packedLight, packedOverlay);

        poseStack.rotateAround(new Quaternionf().rotateY(
                (float) Math.toRadians(720.0f * Easing.IN_OUT_QUAD.ease(blockEntity.getValve(partialTick)))
        ), 0.5F, 0, 0.5F);
        this.renderBakedModel(poseStack, consumer, poseStack.last(), valve_handle_double, rnd, RenderType.SOLID, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void renderBakedModel(PoseStack poseStack, VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, RenderType renderType, int packedLight, int packedOverlay) {
        for (Direction d : SI.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, renderType);
            for (BakedQuad quad : quads) {
                float b = SI.brightnessByDirection(quad.getDirection());
                consumer.putBulkData(last, quad, b, b, b, 1.0F, packedLight, packedOverlay);
            }
        }
    }

    @Override
    public boolean shouldRender(SealedHatchBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }
}
