package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.softcoded.recipe.PressingRecipe;
import org.jetbrains.annotations.Nullable;

public class PressingRecipeCategory implements IRecipeCategory<RecipeHolder<PressingRecipe>> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/press/gui.png");

    private final IDrawable icon, background;
    public PressingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BMNWItems.PRESS.get()));
        this.background = guiHelper.createDrawable(GUI_TEXTURE, 55, 16, 82, 54);
    }

    @Override
    public RecipeType<RecipeHolder<PressingRecipe>> getRecipeType() {
        return BMNWRecipeTypes.PRESSING.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.pressing");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PressingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 1).addIngredients(recipe.value().stamp().asIngredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37).addIngredients(recipe.value().input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(recipe.value().result());
    }

    @Override
    public void draw(RecipeHolder<PressingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
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
}
