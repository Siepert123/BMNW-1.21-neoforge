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
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.decoration.BaseSmallLampBlock;
import nl.melonstudios.bmnw.block.entity.SmallLampBlockEntity;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.misc.PartialModel;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SmallLampRenderer implements BlockEntityRenderer<SmallLampBlockEntity> {
    public SmallLampRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(SmallLampBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        BaseSmallLampBlock block = (BaseSmallLampBlock) state.getBlock();
        Direction facing = state.getValue(BaseSmallLampBlock.FACING);
        PartialModel model = block.getLampPart();
        DyeColor color = block.color;

        poseStack.pushPose();
        poseStack.rotateAround(facing.getRotation(), 0.5F, 0.5F, 0.5F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        PoseStack.Pose last = poseStack.last();
        boolean active = blockEntity.shouldRender();
        float red = FastColor.ARGB32.red(color.getTextureDiffuseColor()) / 255.0F;
        float green = FastColor.ARGB32.green(color.getTextureDiffuseColor()) / 255.0F;
        float blue = FastColor.ARGB32.blue(color.getTextureDiffuseColor()) / 255.0F;
        if (!active) {
            red *= 0.75F;
            green *= 0.75F;
            blue *= 0.75F;
        }

        if (active) {
            for (BakedQuad quad : model.loadAndGet().getQuads(null, null, RandomSource.create(), ModelData.EMPTY, RenderType.solid())) {
                consumer.putBulkData(last, quad, red, green, blue, 1.0F, 0xf000f0, packedOverlay);
            }
        } else {
            this.renderShaded(model.loadAndGet(), consumer, last, red, green, blue, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    @Nonnull
    @Override
    public AABB getRenderBoundingBox(SmallLampBlockEntity blockEntity) {
        return blockEntity.getRenderAABB();
    }

    @Override
    public boolean shouldRender(SmallLampBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    private void renderShaded(BakedModel model, VertexConsumer consumer, PoseStack.Pose last,
                              float r, float g, float b, int packedLight, int packedOverlay) {
        for (BakedQuad quad : model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, RenderType.solid())) {
            float mul = Library.BRIGHTNESS_BY_AXIS[quad.getDirection().getAxis().ordinal()];
            consumer.putBulkData(last, quad, mul * r, mul * g, mul * b, 1.0F, packedLight, packedOverlay);
        }
    }
}
