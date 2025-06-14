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
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.ShreddingRecipe;
import org.joml.Matrix4f;

import java.util.Arrays;

public class ShreddingRecipeCategory extends AbstractRecipeCategory<ShreddingRecipe> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/jei/shredder.png");
    public ShreddingRecipeCategory() {
        super(BMNWRecipeTypes.SHREDDING, Component.translatable("recipe.bmnw.shredding"), null, 54, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShreddingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 1)
                .addItemStacks(Arrays.asList(recipe.input().getItems()));
        builder.addOutputSlot(37, 1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(ShreddingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = 0;
        int y = 0;
        int u = 0;
        int v = 0;
        int width = 54;
        int height = 18;
        float f = 1.0F / width;
        float f1 = 1.0F / height;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = graphics.pose().last().pose();
        bufferBuilder.addVertex(matrix, x, y + height, 0).setUv(u * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y + height, 0).setUv((u + (float) width) * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y, 0).setUv((u + (float) width) * f, v * f1);
        bufferBuilder.addVertex(matrix, x, y, 0).setUv(u * f, v * f1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    private void drawBlit(ResourceLocation texture, int x, int y, int u, int v, int w, int h, GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        float f = 1.0F / w;
        float f1 = 1.0F / h;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = graphics.pose().last().pose();
        bufferBuilder.addVertex(matrix, x, y + h, 0).setUv(u * f, (v + (float) h) * f1);
        bufferBuilder.addVertex(matrix, x + w, y + h, 0).setUv((u + (float) w) * f, (v + (float) h) * f1);
        bufferBuilder.addVertex(matrix, x + w, y, 0).setUv((u + (float) w) * f, v * f1);
        bufferBuilder.addVertex(matrix, x, y, 0).setUv(u * f, v * f1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
