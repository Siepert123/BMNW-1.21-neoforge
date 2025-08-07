package nl.melonstudios.bmnw.weapon.missile.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.weapon.missile.MissileBlueprint;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class CustomizableMissileRenderer extends EntityRenderer<CustomizableMissileEntity> {
    public CustomizableMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private final RandomSource rnd = RandomSource.create();
    private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    private final Quaternionf quat = new Quaternionf();
    private Quaternionf quat() {
        return this.quat.set(0, 0, 0, 1);
    }

    @Override
    public void render(CustomizableMissileEntity missile, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        MissileBlueprint blueprint = missile.getBlueprint();
        if (!blueprint.validationFlag) return;
        int heightThruster = blueprint.thruster.getHeight();
        int heightFuselage = blueprint.fuselage.getHeight();
        int heightWarhead = blueprint.warhead.getHeight();
        int heightTotal = heightThruster + heightFuselage + heightWarhead;
        float half = heightTotal * 0.5F;

        BakedModel thruster = MissileSystem.Client.getOnTheFly(blueprint.thruster);
        BakedModel fins = MissileSystem.Client.getOnTheFly(blueprint.fins);
        BakedModel fuselage = MissileSystem.Client.getOnTheFly(blueprint.fuselage);
        BakedModel warhead = MissileSystem.Client.getOnTheFly(blueprint.warhead);


        VertexConsumer consumer = bufferSource.getBuffer(RenderType.SOLID);
        this.mutable.set(missile.getBlockX(), missile.getBlockY(), missile.getBlockZ());

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);

        poseStack.rotateAround(this.quat().rotateY(Mth.DEG_TO_RAD*(-missile.getViewYRot(partialTick))), 0, 0, 0);
        poseStack.rotateAround(this.quat().rotateX(Mth.DEG_TO_RAD*(missile.getViewXRot(partialTick)-90)), 0, 0, 0);

        poseStack.translate(-0.5F, -half, -0.5F);
        this.renderBakedModel(consumer, poseStack.last(), thruster,
                this.rnd, packedLight);
        poseStack.translate(0.0F, heightThruster, 0.0F);
        this.renderBakedModel(consumer, poseStack.last(), fins,
                this.rnd, packedLight);
        this.renderBakedModel(consumer, poseStack.last(), fuselage,
                this.rnd, packedLight);
        poseStack.translate(0.0F, heightFuselage, 0.0F);
        this.renderBakedModel(consumer, poseStack.last(), warhead,
                this.rnd, packedLight);
        poseStack.popPose();
    }

    private void renderBakedModel(VertexConsumer consumer, PoseStack.Pose last, BakedModel model,
                                  RandomSource rnd, int packedLight) {
        for (Direction d : Library.DIRECTIONS_WITH_NULL) {
            List<BakedQuad> quads = model.getQuads(null, d, rnd, ModelData.EMPTY, RenderType.SOLID);
            for (BakedQuad quad : quads) {
                consumer.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(CustomizableMissileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
