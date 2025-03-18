package nl.melonstudios.bmnw.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique private static final ResourceLocation SUN_LOCATION_CUSTOM = BMNW.namespace("textures/item/playstation.png");
    @Unique private static final ResourceLocation SUN_LOCATION_CUSTOM_2 = BMNW.namespace("textures/block/alloy_blast_furnace_front_active.png");
    @Unique private static boolean other = BMNW.memzArguments % 2 == 1;

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "renderSky", at = @At("RETURN"))
    public void renderSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        Tesselator tesselator = Tesselator.getInstance();
        PoseStack posestack = new PoseStack();
        posestack.mulPose(frustumMatrix);
        if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );
            posestack.pushPose();
            float f11 = 1.0F - this.level.getRainLevel(partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
            posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            posestack.mulPose(Axis.XP.rotationDegrees(this.level.getTimeOfDay(partialTick) * 360.0F));
            if (BMNW.memz) { //Replace the sun if MemzMode is enable
                Matrix4f matrix4f1 = posestack.last().pose();
                float f12 = 30.0F / 4;
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, other ? SUN_LOCATION_CUSTOM_2 : SUN_LOCATION_CUSTOM);
                BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(1.0F, 0.0F);
                bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(0.0F, 0.0F);
                bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, f12).setUv(0.0F, 1.0F);
                bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(1.0F, 1.0F);
                BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
            }
        }
    }
}
