package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.recipe.RecipeType;

public class BMNWRecipeTypes {
    public static final RecipeType<PressingRecipe> PRESSING =
            RecipeType.create("bmnw", "pressing", PressingRecipe.class);
}
