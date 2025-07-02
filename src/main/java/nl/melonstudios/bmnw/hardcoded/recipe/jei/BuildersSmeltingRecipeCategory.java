package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class BuildersSmeltingRecipeCategory implements IRecipeCategory<RecipeHolder<BuildersSmeltingRecipe>> {
    public static final ResourceLocation UID = BMNW.namespace("builders_smelting");
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/builders_furnace.png");

    private final IDrawable background;
    private final IDrawable icon;

    public BuildersSmeltingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 55, 16, 82, 54);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BMNWItems.BUILDERS_FURNACE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<BuildersSmeltingRecipe>> getRecipeType() {
        return BMNWRecipeTypes.BUILDERS_SMELTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.builders_smelting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BuildersSmeltingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.value().input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(recipe.value().output());
    }

    @Override
    public void draw(RecipeHolder<BuildersSmeltingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 82;
    }
    @Override
    public int getHeight() {
        return 54;
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
}
