package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.entity.MeteoriteEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.SI;

@OnlyIn(Dist.CLIENT)
public class MeteoriteRenderer extends EntityRenderer<MeteoriteEntity> {
    protected BlockRenderDispatcher dispatcher;
    public MeteoriteRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public ResourceLocation getTextureLocation(MeteoriteEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(MeteoriteEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        BlockState state = BMNWBlocks.HOT_METEORITE_COBBLESTONE.get().defaultBlockState();

        poseStack.pushPose();
        poseStack.scale(3, 3, 3);
        poseStack.translate(-1.5F, -1.5F, -1.5F);

        RandomSource rnd = RandomSource.create();
        BakedModel model = BMNWPartialModels.METEORITE.loadAndGet();
        PoseStack.Pose last = poseStack.last();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);
        for (Direction d : SI.DIRECTIONS_WITH_NULL) {
            for (BakedQuad quad : model.getQuads(state, d, rnd, ModelData.EMPTY, RenderType.SOLID)) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, 0xf000f0, OverlayTexture.NO_OVERLAY);
            }
        }
        poseStack.popPose();
    }
}
