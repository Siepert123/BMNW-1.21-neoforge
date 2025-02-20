package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.entity.custom.MeteoriteEntity;

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
        poseStack.translate(-1.5, -1.5, -1.5);
        poseStack.scale(3, 3, 3);
        //poseStack.rotateAround(p_entity.rotation, 0f, 0f, 0f);

        BakedModel model = this.dispatcher.getBlockModel(state);
        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(BlockPos.ZERO)), ModelData.EMPTY))
            this.dispatcher
                    .getModelRenderer()
                    .tesselateWithoutAO(
                            p_entity.level(),
                            this.dispatcher.getBlockModel(state),
                            state,
                            new BlockPos(0, 256, 0),
                            poseStack,
                            bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                            false,
                            RandomSource.create(),
                            state.getSeed(BlockPos.ZERO),
                            OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY,
                            renderType
                    );
        poseStack.popPose();
    }
}
