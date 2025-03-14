package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.screen.AlloyFurnaceScreen;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Arrays;

public class AlloyingRecipeCategory extends AbstractRecipeCategory<AlloyingRecipe> {
    public static final ResourceLocation GUI_TEXTURE = AlloyFurnaceScreen.GUI_TEXTURE;
    public AlloyingRecipeCategory() {
        super(BMNWRecipeTypes.ALLOYING, Component.translatable("recipe.bmnw.alloy_blast_furnace"), null, 76, 54);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(66-64, 16-14)
                .addItemStacks(Arrays.asList(recipe.input1().getItems()));
        builder.addInputSlot(66-64, 50-14)
                .addItemStacks(Arrays.asList(recipe.input2().getItems()));
        builder.addOutputSlot(118-64, 33-14)
                .addItemStack(recipe.result());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(AlloyingRecipe recipe) {
        return recipe.rsl();
    }

    @Override
    public void draw(AlloyingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = 0;
        int y = 0;
        int u = 64;
        int v = 14;
        int width = 76;
        int height = 54;
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

        this.drawBlit(GUI_TEXTURE, 20, 8, 176, 14,
                this.scaledProgress((int)(System.currentTimeMillis() / 50) % 100, 27), 37, graphics);
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

    private int scaledProgress(int p, int w) {
        return p * w / 100;
    }
}
