package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.entity.custom.HatchBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
            InventoryMenu.BLOCK_ATLAS, BMNW.namespace("block/hatch")
    );
    @Override
    public void render(HatchBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockstate = be.getBlockState();
        BlockPos pos = be.getBlockPos();
        boolean open = be.isOpen();
        Direction direction = be.getFacing();

        float ticks = be.ticks == 0 ? 0 : be.ticks - partialTick;

        Level level = be.getLevel();
        {
            poseStack.pushPose();
            poseStack.translate(0, 1, 0);
            rotateStack(poseStack, direction, open, ticks);
            BakedModel model = this.dispatcher.getBlockModel(blockstate);
            for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(pos.above())), ModelData.EMPTY))
                this.dispatcher
                        .getModelRenderer()
                        .tesselateWithoutAO(
                                level,
                                this.dispatcher.getBlockModel(blockstate),
                                blockstate,
                                pos.above(),
                                poseStack,
                                bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                                false,
                                RandomSource.create(),
                                blockstate.getSeed(pos.above()),
                                OverlayTexture.NO_OVERLAY,
                                ModelData.EMPTY,
                                renderType
                        );
            poseStack.popPose();
        }

        {
            poseStack.pushPose();
            BakedModel model = this.dispatcher.getBlockModel(be.getOtherPart());
            for (RenderType renderType : model.getRenderTypes(be.getOtherPart(), RandomSource.create(be.getOtherPart().getSeed(pos)), ModelData.EMPTY))
                this.dispatcher
                        .getModelRenderer()
                        .tesselateBlock(
                                level,
                                this.dispatcher.getBlockModel(be.getOtherPart()),
                                be.getOtherPart(),
                                pos,
                                poseStack,
                                bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                                true,
                                RandomSource.create(),
                                be.getOtherPart().getSeed(pos),
                                OverlayTexture.NO_OVERLAY,
                                ModelData.EMPTY,
                                renderType
                        );
            poseStack.popPose();
        }
    }
    private void rotateStack(PoseStack poseStack, Direction facing, boolean open, float ticks) {
        float angle = open ? (float) Math.toRadians(ticks / 20 * 90 - 90) : (float) Math.toRadians(-ticks / 20 * 90);
        switch (facing) {
            case SOUTH -> poseStack.rotateAround(new Quaternionf().rotateAxis(angle, 1, 0, 0), 0, 0, 0);
            case NORTH -> {
                poseStack.translate(0, 0, 1);
                poseStack.mulPose(new Quaternionf().rotateAxis(-angle, 1, 0, 0));
                poseStack.translate(0, 0, -1);
            }
            case WEST -> {
                poseStack.translate(1, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, 0, 0, 1));
                poseStack.translate(-1, 0, 0);
            }
            case EAST -> poseStack.rotateAround(new Quaternionf().rotateAxis(-angle, 0, 0, 1), 0, 0, 0);
        }
    }

    @Override
    public boolean shouldRender(HatchBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }
}
