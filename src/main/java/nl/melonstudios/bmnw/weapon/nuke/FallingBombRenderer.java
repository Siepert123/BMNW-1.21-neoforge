package nl.melonstudios.bmnw.weapon.nuke;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.misc.PartialModel;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FallingBombRenderer extends EntityRenderer<FallingBombEntity> {
    public FallingBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static float getRotationFromFacing(Direction facing) {
        return switch (facing) {
            case EAST -> 4.712389f;
            case SOUTH -> 3.1415927f;
            case WEST -> 1.5707964f;
            default -> 0.0F;
        };
    }

    @Override
    public void render(FallingBombEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY(getRotationFromFacing(p_entity.getNukeState().getValue(NukeBlock.FACING))),
                0.0F, 0.0F, 0.0F);
        if (p_entity.getNukeBlock().fancyDrop()) {
            poseStack.rotateAround(new Quaternionf().rotateX((float) p_entity.getDeltaMovement().y * 0.5F), 0.5F, 0.5F, 0.5F);
        }
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        PartialModel model = p_entity.getNukeBlock().getDroppedModel();
        BakedModel bakedModel = model.loadAndGet();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.CUTOUT);

        this.renderBakedModel(consumer, poseStack.last(), bakedModel, RandomSource.create(), packedLight);

        poseStack.popPose();
    }

    private void renderBakedModel(VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, int packedLight) {
        for (Direction d : Library.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.CUTOUT);
            for (BakedQuad quad : quads) {
                float b = Library.brightnessByDirection(quad.getDirection());
                consumer.putBulkData(last, quad, b, b, b, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FallingBombEntity fallingBombEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
