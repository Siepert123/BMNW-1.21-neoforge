package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import nl.melonstudios.bmnw.entity.custom.BlockDebrisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class BlockDebrisRenderer extends EntityRenderer<BlockDebrisEntity> {
    protected BlockRenderDispatcher dispatcher;
    public BlockDebrisRenderer(EntityRendererProvider.Context context) {
        super(context);
        dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BlockDebrisEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        BlockState blockstate = entity.getDebrisState();
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level();
            if (blockstate != level.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                poseStack.pushPose();
                BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                poseStack.rotateAround(new Quaternionf(new AxisAngle4d(entity.getId() + entity.getX() + entity.getZ() + (entity.getY() / 5), 0.5f, 0.5f, 0.5f)), 0.5f, 0.5f, 0.5f);
                poseStack.translate(-0.5, 0.0, -0.5);
                var model = this.dispatcher.getBlockModel(blockstate);
                for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(BlockPos.ZERO)), net.neoforged.neoforge.client.model.data.ModelData.EMPTY))
                    this.dispatcher
                            .getModelRenderer()
                            .tesselateBlock(
                                    level,
                                    this.dispatcher.getBlockModel(blockstate),
                                    blockstate,
                                    blockpos,
                                    poseStack,
                                    bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                                    false,
                                    RandomSource.create(),
                                    blockstate.getSeed(BlockPos.ZERO),
                                    OverlayTexture.NO_OVERLAY,
                                    net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                                    renderType
                            );
                poseStack.popPose();
                super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BlockDebrisEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
