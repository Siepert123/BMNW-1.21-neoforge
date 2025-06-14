package nl.melonstudios.bmnw.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.block.entity.WireAttachedBlockEntity;
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

        VertexConsumer leashConsumer = bufferSource.getBuffer(RenderType.LEASH);
        Matrix4f leashMatrix = poseStack.last().pose();
        Level level = blockEntity.getLevel();

        assert level != null;

        int sourceSky = level.getBrightness(LightLayer.SKY, sourcePos);
        int sourceBlock = level.getBrightness(LightLayer.BLOCK, sourcePos);

        for (Vec3 connection : connections) {
            poseStack.pushPose();
            BlockPos connectionPos = BlockPos.containing(connection);
            int connectionSky = level.getBrightness(LightLayer.SKY, connectionPos);
            int connectionBlock = level.getBrightness(LightLayer.BLOCK, connectionPos);

            float x = (float) (connection.x - source.x);
            float y = (float) (connection.y - source.y);
            float z = (float) (connection.z - source.z);

            for (int i = 0; i < 24; i++) {
                addVertexPair(
                        leashConsumer, leashMatrix, x, y, z,
                        connectionBlock, sourceBlock,
                        connectionSky, sourceSky,
                        0, 0, 0, 0,
                        i, false
                );
            }
            for (int i = 0; i < 24; i++) {
                addVertexPair(
                        leashConsumer, leashMatrix, x, y, z,
                        connectionBlock, sourceBlock,
                        connectionSky, sourceSky,
                        0, 0, 0, 0,
                        i, true
                );
            }

            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    public static void addVertexPair(
            VertexConsumer buffer,
            Matrix4f pose,
            float startX,
            float startY,
            float startZ,
            int entityBlockLight,
            int holderBlockLight,
            int entitySkyLight,
            int holderSkyLight,
            float yOffset,
            float dy,
            float dx,
            float dz,
            int index,
            boolean reverse
    ) {
        float f = (float)index / 24.0F;
        int i = (int) Mth.lerp(f, (float)entityBlockLight, (float)holderBlockLight);
        int j = (int)Mth.lerp(f, (float)entitySkyLight, (float)holderSkyLight);
        int k = LightTexture.pack(i, j);
        float f1 = index % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = startX * f;
        float f6 = startY > 0.0F ? startY * f * f : startY - startY * (1.0F - f) * (1.0F - f);
        float f7 = startZ * f;
        buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(f2, f3, f4, 1.0F).setLight(k);
        buffer.addVertex(pose, f5 + dx, f6 + yOffset - dy, f7 - dz).setColor(f2, f3, f4, 1.0F).setLight(k);
    }
}
