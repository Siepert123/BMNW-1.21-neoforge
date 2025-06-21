package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.block.entity.WireAttachedBlockEntity;
import nl.melonstudios.bmnw.cfg.BMNWClientConfig;
import nl.melonstudios.bmnw.misc.Library;
import org.joml.Vector3fc;

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
        Vec3 color = blockEntity.getWireColor();
        float r = (float)color.x;
        float g = (float)color.y;
        float b = (float)color.z;
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
                    source, connection, r, g, b, poseStack, bufferSource,
                    sourceSky, sourceBlock, connectionSky, connectionBlock,
                    false, 24, Boolean.FALSE
            );

            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return BMNWClientConfig.wireViewDistance();
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 cameraPos) {
        return BMNWClientConfig.wireViewDistance() == Short.MAX_VALUE || super.shouldRender(blockEntity, cameraPos);
    }
}
