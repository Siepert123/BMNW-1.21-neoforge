package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;

public class BuildersSmeltingRecipeCategory extends AbstractRecipeCategory<BuildersSmeltingRecipe> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/builders_furnace.png");

    public BuildersSmeltingRecipeCategory(RecipeType<BuildersSmeltingRecipe> recipeType, Component title, IDrawable icon, int width, int height) {
        super(recipeType, title, icon, width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BuildersSmeltingRecipe recipe, IFocusGroup focuses) {

    }
}
