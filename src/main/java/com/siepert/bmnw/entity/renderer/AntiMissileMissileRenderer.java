package com.siepert.bmnw.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.siepert.bmnw.entity.custom.AntiMissileMissileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

public class AntiMissileMissileRenderer extends EntityRenderer<AntiMissileMissileEntity> {
    @Nonnull
    private BlockState getMissileState() {
        return Blocks.ANVIL.defaultBlockState();
    }
    public AntiMissileMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AntiMissileMissileEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vec3 look = entity.getLookAngle();
        poseStack.rotateAround(new Quaternionf(look.x, look.y, look.z, 1), 0.5f, 0.5f, 0.5f);

        poseStack.translate(-0.5f, 0, -0.5f);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(getMissileState(), poseStack, bufferSource,
                packedLight, packedLight, ModelData.builder().build(), RenderType.SOLID);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(AntiMissileMissileEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
