package nl.melonstudios.bmnw.weapon.nuke;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FallingBombRenderer extends EntityRenderer<FallingBombEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    public FallingBombRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
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

        BlockState blockstate = p_entity.getNukeBlock().defaultBlockState();
        Library.renderBlockThing(this.blockRenderDispatcher, blockstate, p_entity.blockPosition(), p_entity.level(),
                poseStack, bufferSource, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FallingBombEntity fallingBombEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
