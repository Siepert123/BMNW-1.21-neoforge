package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.screen.AlloyFurnaceScreen;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkbenchRecipeCategory extends AbstractRecipeCategory<WorkbenchRecipe> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/workbench/jei.png");
    public WorkbenchRecipeCategory() {
        super(BMNWRecipeTypes.WORKBENCH, Component.translatable("recipe.bmnw.workbench"), null, 92, 57);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WorkbenchRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.ingredients();
        Map<Ingredient, Integer> nameToInt = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            nameToInt.putIfAbsent(ingredient, 0);
            nameToInt.computeIfPresent(ingredient, (k, v) -> v + 1);
        }
        int id = 0;
        for (Map.Entry<Ingredient, Integer> entry : nameToInt.entrySet()) {
            int x = 2+(id%5)*18;
            int y = 2+(id/5)*18;
            id++;
            ItemStack[] array = entry.getKey().getItems();
            ItemStack[] copy = new ItemStack[array.length];
            for (int i = 0; i < array.length; i++) {
                copy[i] = array[i].copy();
                copy[i].setCount(entry.getValue());
            }
            builder.addInputSlot(x, y).addItemStacks(Arrays.asList(copy));
        }
        builder.addOutputSlot(56, 39)
                .addItemStack(recipe.result());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(WorkbenchRecipe recipe) {
        return recipe.rsl();
    }

    @Override
    public void draw(WorkbenchRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = 0;
        int y = 0;
        int u = 0;
        int v = 0;
        int width = 92;
        int height = 57;
        float f = 1.0F / width;
        float f1 = 1.0F / height;

        PoseStack poseStack = graphics.pose();

        poseStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();
        bufferBuilder.addVertex(matrix, x, y + height, 0).setUv(u * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y + height, 0).setUv((u + (float) width) * f, (v + (float) height) * f1);
        bufferBuilder.addVertex(matrix, x + width, y, 0).setUv((u + (float) width) * f, v * f1);
        bufferBuilder.addVertex(matrix, x, y, 0).setUv(u * f, v * f1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Font font = Minecraft.getInstance().font;
        String tierText = "Tier " + (recipe.minTier()+1);
        int w = font.width(tierText);
        graphics.drawString(font, tierText, 56 - w/2, 90, 0xFFFFFF00);
        poseStack.popPose();
    }
}
