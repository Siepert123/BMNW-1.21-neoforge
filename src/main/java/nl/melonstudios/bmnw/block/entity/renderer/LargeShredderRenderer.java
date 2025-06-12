package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.entity.LargeShredderBlockEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class LargeShredderRenderer implements BlockEntityRenderer<LargeShredderBlockEntity> {
    @Override
    public void render(LargeShredderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(blockEntity.getFacing().toYRot())), 0.5F, 0.5F, 0.5F);

        //TODO: render the rotating shredding part

        //TODO: render the closing thingy

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(LargeShredderBlockEntity blockEntity) {
        if (blockEntity.renderBB == null) {
            blockEntity.renderBB = new AABB(blockEntity.getBlockPos()).inflate(1);
        }
        return blockEntity.renderBB;
    }

    @Override
    public int getViewDistance() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldRender(LargeShredderBlockEntity blockEntity, Vec3 cameraPos) {
        return !blockEntity.multiblockSlave;
    }
}
