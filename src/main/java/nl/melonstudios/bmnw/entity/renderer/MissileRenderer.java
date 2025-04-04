package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.entity.MissileEntity;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public abstract class MissileRenderer<T extends MissileEntity> extends EntityRenderer<T> {
    private BlockState missileState;
    protected MissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    public void setMissileState(BlockState state) {
        this.missileState = state;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (missileState == null) return;
        poseStack.pushPose();

        if (entity.isFalling()) {
            poseStack.rotateAround(new Quaternionf(new AxisAngle4d(Math.toRadians(180), 1, 0, 0)), 1, 0, 0);
        }
        poseStack.translate(-0.5, /*Mth.lerp(partialTick, entity.getSpeed(), 0)*/ 0, -0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(missileState, poseStack, bufferSource, packedLight, packedLight, ModelData.builder().build(), RenderType.SOLID);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
