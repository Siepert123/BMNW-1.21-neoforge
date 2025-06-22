package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.entity.RadioAntennaControllerBlockEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.Library;

public class RadioAntennaControllerRenderer extends OptimizedBlockEntityRenderer<RadioAntennaControllerBlockEntity> {
    public RadioAntennaControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RadioAntennaControllerBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        RadioAntennaControllerBlockEntity.StructureData data = blockEntity.getStructureData();
        BakedModel model = BMNWPartialModels.VERTICAL_CABLE_REDSTONE.loadAndGet();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);
        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        for (int i = 0; i < data.getHeight(); i++) {
            poseStack.translate(0, 1, 0);
            this.renderBakedModel(poseStack.last(), model, consumer, LightTexture.FULL_BRIGHT, packedOverlay);
        }
        poseStack.popPose();
    }

    private void renderBakedModel(PoseStack.Pose last, BakedModel model, VertexConsumer consumer, int light, int overlay) {
        RandomSource rnd = RandomSource.create(0L);
        for (Direction d : Library.DIRECTIONS_WITH_NULL) {
            for (BakedQuad quad : model.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.SOLID)) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
            }
        }
    }

    @Override
    public boolean shouldRender(RadioAntennaControllerBlockEntity blockEntity, Vec3 cameraPos) {
        return blockEntity.getStructureData().isValid();
    }
}
