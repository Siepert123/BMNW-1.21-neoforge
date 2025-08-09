package nl.melonstudios.bmnw.weapon.nuke.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.entity.renderer.OptimizedBlockEntityRenderer;
import nl.melonstudios.bmnw.init.BMNWCache;
import nl.melonstudios.bmnw.misc.Library;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DroppedSoulfireBombBER extends OptimizedBlockEntityRenderer<DroppedSoulfireBombBE> {
    private final BlockRenderDispatcher dispatcher;
    public DroppedSoulfireBombBER(BlockEntityRendererProvider.Context context) {
        super(context);

        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(DroppedSoulfireBombBE blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.rotateAround(BMNWCache.getDudRotation(blockEntity.getBlockPos()), 0.5F, 0.5F, 0.5F);

        Library.renderBlockThing(this.dispatcher, blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity.getLevel(),
                poseStack, bufferSource, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(DroppedSoulfireBombBE be, Vec3 cameraPos) {
        return true;
    }
}
