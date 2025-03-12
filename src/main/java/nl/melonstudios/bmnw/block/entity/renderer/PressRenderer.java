package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.entity.PressBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;

public class PressRenderer implements BlockEntityRenderer<PressBlockEntity> {
    protected final BlockRenderDispatcher dispatcher;
    public PressRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }
    @Override
    public void render(PressBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        boolean downNow = blockEntity.progress == 40;
        boolean downThen = blockEntity.progressOld == 40;

        BlockState state = BMNWBlocks.PRESS_HEAD.get().defaultBlockState();
        BlockPos pos = blockEntity.getBlockPos().above();
        Level level = blockEntity.getLevel();
        BakedModel model = this.dispatcher.getBlockModel(BMNWBlocks.PRESS.get().defaultBlockState());
        poseStack.pushPose();
        poseStack.translate(0, 1.875, 0);
        if (downNow) poseStack.translate(0, -(partialTick*0.875), 0);
        else if (downThen) poseStack.translate(0, (partialTick*0.875)-1, 0);
        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY))
            this.dispatcher
                    .getModelRenderer()
                    .tesselateWithoutAO(
                            level,
                            this.dispatcher.getBlockModel(state),
                            state,
                            pos.above(),
                            poseStack,
                            bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                            false,
                            RandomSource.create(),
                            state.getSeed(pos),
                            OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY,
                            renderType
                    );
        poseStack.popPose();
    }
}
