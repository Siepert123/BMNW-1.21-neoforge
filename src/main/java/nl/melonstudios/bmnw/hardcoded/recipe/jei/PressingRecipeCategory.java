package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.screen.PressScreen;
import org.joml.Matrix4f;

import java.util.Arrays;

public class PressingRecipeCategory extends AbstractRecipeCategory<PressingRecipe> {
    public static final ResourceLocation GUI_TEXTURE = PressScreen.GUI_TEXTURE;
    public PressingRecipeCategory() {
        super(BMNWRecipeTypes.PRESSING, Component.translatable("recipe.bmnw.pressing"), null, 84, 56);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PressingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(56-54, 17-15)
                .addItemStacks(recipe.moldType().asStackList());
        builder.addInputSlot(56-54, 53-15)
                .addItemStacks(Arrays.asList(recipe.input().getItems()));
        builder.addOutputSlot(116-54, 35-15)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(PressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = 0;
        int y = 0;
        int u = 54;
        int v = 15;
        int width = 84;
        int height = 56;
        float f = 1.0F / 256;
        float f1 = 1.0F / 256;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = graphics.pose().last().pose();
        bufferBuilder.addVertex(matrix, x, y + height, 0).setUv(u * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y + height, 0).setUv((u + (float) width) * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y, 0).setUv((u + (float) width) * f, v * f1);
        bufferBuilder.addVertex(matrix, x, y, 0).setUv(u * f, v * f1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        this.drawBlit(GUI_TEXTURE, 55-54,35-15, 176, 14,
                18, this.scaledProgress(((int)(System.currentTimeMillis() / 50) % 40), 17), graphics);
    }

    private void drawBlit(ResourceLocation texture, int x, int y, int u, int v, int w, int h, GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        float f = 1.0F / 256;
        float f1 = 1.0F / 256;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = graphics.pose().last().pose();
        bufferBuilder.addVertex(matrix, x, y + h, 0).setUv(u * f, (v + (float) h) * f1);
        bufferBuilder.addVertex(matrix, x + w, y + h, 0).setUv((u + (float) w) * f, (v + (float) h) * f1);
        bufferBuilder.addVertex(matrix, x + w, y, 0).setUv((u + (float) w) * f, v * f1);
        bufferBuilder.addVertex(matrix, x, y, 0).setUv(u * f, v * f1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private int scaledProgress(int p, int h) {
        return p * h / 40;
    }
}
