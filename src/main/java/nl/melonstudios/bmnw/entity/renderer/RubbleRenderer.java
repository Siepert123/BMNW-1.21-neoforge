package nl.melonstudios.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.entity.RubbleEntity;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class RubbleRenderer extends EntityRenderer<RubbleEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final ModelBlockRenderer modelBlockRenderer;
    public RubbleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        this.modelBlockRenderer = context.getBlockRenderDispatcher().getModelRenderer();
    }

    private static final Quaternionf ROTATE_SPIN_180 = new Quaternionf().rotateX(180);

    @Override
    public void render(RubbleEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.5F, 0, -0.5F);

        BakedModel model = BMNWPartialModels.METAL_DOOR_HANDLE.loadAndGet();

        poseStack.rotateAround(ROTATE_SPIN_180, 0.5F, 0.5F, 0.5F);
        float rot = ((p_entity.tickCount + partialTick) % 360) * 10;
        poseStack.rotateAround(new Quaternionf().rotateXYZ(rot, rot, rot), 0.5F, 0.5F, 0.5F);
        try {
            BlockState state = p_entity.getBlockState();
            TextureAtlasSprite sprite = this.blockRenderDispatcher.getBlockModel(state).getParticleIcon(ModelData.EMPTY);
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);
            PoseStack.Pose last = poseStack.last();
            for (BakedQuad quad : model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, RenderType.SOLID))  {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F,
                        packedLight, OverlayTexture.pack(sprite.getX(), sprite.getY()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(RubbleEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
