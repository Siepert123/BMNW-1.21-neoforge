package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import nl.melonstudios.bmnw.hardcoded.recipe.ShreddingRecipe;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;

public class BMNWRecipeTypes {
    public static final RecipeType<WorkbenchRecipe> WORKBENCH =
            RecipeType.create("bmnw", "workbench", WorkbenchRecipe.class);
    public static final RecipeType<PressingRecipe> PRESSING =
            RecipeType.create("bmnw", "pressing", PressingRecipe.class);
    public static final RecipeType<AlloyingRecipe> ALLOYING =
            RecipeType.create("bmnw", "alloying", AlloyingRecipe.class);
    public static final RecipeType<BuildersSmeltingRecipe> BUILDERS_SMELTING =
            RecipeType.create("bmnw", "builders_smelting", BuildersSmeltingRecipe.class);
    public static final RecipeType<ShreddingRecipe> SHREDDING =
            RecipeType.create("bmnw", "shredding", ShreddingRecipe.class);
}
