package com.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.melonstudios.bmnw.BMNW;
import com.melonstudios.bmnw.block.custom.HatchBlock;
import com.melonstudios.bmnw.block.entity.custom.HatchBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class HatchRenderer implements BlockEntityRenderer<HatchBlockEntity> {
    protected BlockRenderDispatcher dispatcher;
    public HatchRenderer(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getBlockRenderDispatcher();
    }
    public static final Material HATCH_RESOURCE_LOCATION = new Material(
            TextureAtlas.LOCATION_BLOCKS, BMNW.namespace("block/hatch")
    );
    @Override
    public void render(HatchBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        BlockPos pos = blockEntity.getBlockPos();
        boolean open = state.getValue(HatchBlock.OPEN);

        float ticks = blockEntity.ticks - partialTick;

        //poseStack.pushPose();
        if (open) poseStack.rotateAround(new Quaternionf(0, 1, 0, ticks), 0, 0, 0);
        Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(state, pos, blockEntity.getLevel(), poseStack, bufferSource.getBuffer(RenderType.SOLID));
        //poseStack.popPose();
    }
}
