package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.entity.MultiblockDebrisEntity;
import org.joml.Quaternionf;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MultiblockDebrisRenderer extends EntityRenderer<MultiblockDebrisEntity> {
    protected final BlockRenderDispatcher dispatcher;
    protected final ModelBlockRenderer renderer;
    public MultiblockDebrisRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getBlockRenderDispatcher();
        this.renderer = this.dispatcher.getModelRenderer();
    }

    @Override
    public void render(MultiblockDebrisEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.5F, 0.25F, -0.5F);
        RandomSource rnd = RandomSource.create();
        Level level = entity.level();
        float rot = ((entity.tickCount + partialTick) % 360);
        poseStack.rotateAround(new Quaternionf().rotateXYZ(rot, rot, rot), 0.5F, 0.5F, 0.5F);
        for (Map.Entry<BlockPos, BlockState> entry : entity.getStructure().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (state.getRenderShape() != RenderShape.MODEL) continue;
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            BakedModel model = this.dispatcher.getBlockModel(state);
            for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
                this.renderer.tesselateBlock(
                        level,
                        model,
                        state,
                        pos,
                        poseStack,
                        bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)),
                        false,
                        rnd,
                        state.getSeed(pos),
                        OverlayTexture.NO_OVERLAY,
                        ModelData.EMPTY,
                        renderType
                );
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MultiblockDebrisEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
