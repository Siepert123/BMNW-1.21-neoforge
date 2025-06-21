package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.block.entity.OptimizedBlockEntity;
import nl.melonstudios.bmnw.block.entity.WireAttachedBlockEntity;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public class WireAttachedRenderer<T extends WireAttachedBlockEntity> extends OptimizedBlockEntityRenderer<T> {
    public WireAttachedRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Vec3 source = blockEntity.getBlockPos().getCenter();
        BlockPos sourcePos = blockEntity.getBlockPos();
        Collection<Vec3> connections = blockEntity.wireConnectionsForRendering();
        Level level = blockEntity.getLevel();

        assert level != null;

        int sourceSky = level.getBrightness(LightLayer.SKY, sourcePos);
        int sourceBlock = level.getBrightness(LightLayer.BLOCK, sourcePos);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        for (Vec3 connection : connections) {
            poseStack.pushPose();
            BlockPos connectionPos = BlockPos.containing(connection);
            int connectionSky = level.getBrightness(LightLayer.SKY, connectionPos);
            int connectionBlock = level.getBrightness(LightLayer.BLOCK, connectionPos);
            Library.renderLeash(
                    source, connection,
                    1.0F, 1.0F, 1.0F, poseStack, bufferSource,
                    sourceSky, sourceBlock, connectionSky, connectionBlock
            );

            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }
}
