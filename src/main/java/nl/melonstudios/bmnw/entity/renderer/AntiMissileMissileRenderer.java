package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import nl.melonstudios.bmnw.entity.custom.AntiMissileMissileEntity;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class AntiMissileMissileRenderer extends EntityRenderer<AntiMissileMissileEntity> {
    protected BlockRenderDispatcher dispatcher;
    @Nonnull
    private BlockState getMissileState() {
        return Blocks.ANVIL.defaultBlockState();
    }
    public AntiMissileMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AntiMissileMissileEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vec3 look = entity.getLookAngle();
        poseStack.rotateAround(new Quaternionf(look.x, look.y, look.z, 1), 0.5f, 0.5f, 0.5f);

        poseStack.scale(2, 2, 2);
        poseStack.translate(-0.5, -0.5, -0.5);
        BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

        BakedModel model = this.dispatcher.getBlockModel(getMissileState());
        for (var renderType : model.getRenderTypes(getMissileState(), RandomSource.create(getMissileState().getSeed(BlockPos.ZERO)), net.neoforged.neoforge.client.model.data.ModelData.EMPTY))
            this.dispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                            entity.level(),
                            this.dispatcher.getBlockModel(getMissileState()),
                            getMissileState(),
                            blockpos,
                            poseStack,
                            bufferSource.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                            false,
                            RandomSource.create(),
                            getMissileState().getSeed(BlockPos.ZERO),
                            OverlayTexture.NO_OVERLAY,
                            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                            renderType
                    );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AntiMissileMissileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
