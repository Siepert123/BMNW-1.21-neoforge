package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import nl.melonstudios.bmnw.hardcoded.recipe.ShreddingRecipe;

public class ShreddingRecipeCategory extends AbstractRecipeCategory<ShreddingRecipe> {
    /**
     * @param recipeType
     * @param title
     * @param icon
     * @param width
     * @param height
     * @since 19.19.0
     */
    public ShreddingRecipeCategory(RecipeType<ShreddingRecipe> recipeType, Component title, IDrawable icon, int width, int height) {
        super(recipeType, title, icon, width, height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShreddingRecipe recipe, IFocusGroup focuses) {

    }
}
