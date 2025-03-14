package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.block.entity.PressBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;

@OnlyIn(Dist.CLIENT)
public class PressRenderer implements BlockEntityRenderer<PressBlockEntity> {
    protected final BlockRenderDispatcher dispatcher;
    public PressRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }
    @Override
    public void render(PressBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        boolean downNow = blockEntity.progress == 40;
        boolean downThen = blockEntity.progressOld == 40;

        ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
        BlockState state = BMNWBlocks.PRESS_HEAD.get().defaultBlockState();
        BlockPos pos = blockEntity.getBlockPos().above();
        Level level = blockEntity.getLevel();
        BakedModel model = this.dispatcher.getBlockModel(BMNWBlocks.PRESS.get().defaultBlockState());
        poseStack.pushPose();
        ItemStack input = blockEntity.inventory.getStackInSlot(2);
        ItemStack output = blockEntity.inventory.getStackInSlot(3);
        if (!input.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.65f, 0.65f, 0.65f);
            renderItem.render(input, ItemDisplayContext.NONE, false,
                    poseStack, bufferSource, packedLight, packedOverlay,
                    renderItem.getModel(input, blockEntity.getLevel(), null, 0));
            poseStack.popPose();
        } else if (!output.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.65f, 0.65f, 0.65f);
            renderItem.render(output, ItemDisplayContext.NONE, false,
                    poseStack, bufferSource, packedLight, packedOverlay,
                    renderItem.getModel(output, blockEntity.getLevel(), null, 0));
            poseStack.popPose();
        }
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
        ItemStack stamp = blockEntity.inventory.getStackInSlot(1);
        if (!stamp.isEmpty()) {
            poseStack.translate(0.5f, -0.f, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.7f, 0.7f, 0.7f);
            renderItem.render(stamp, ItemDisplayContext.NONE, false,
                    poseStack, bufferSource, packedLight, packedOverlay,
                    renderItem.getModel(stamp, blockEntity.getLevel(), null, 0));
        }
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(PressBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX()+1,
                pos.getY()+3,
                pos.getZ()+1
        );
    }
}
