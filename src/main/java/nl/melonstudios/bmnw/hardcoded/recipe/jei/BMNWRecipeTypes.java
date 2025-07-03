package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.softcoded.recipe.AlloyingRecipe;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;
import nl.melonstudios.bmnw.softcoded.recipe.ShreddingRecipe;

import java.util.function.Supplier;

public class BMNWRecipeTypes {
    public static final RecipeType<WorkbenchRecipe> WORKBENCH =
            RecipeType.create("bmnw", "workbench", WorkbenchRecipe.class);
    public static final RecipeType<PressingRecipe> PRESSING =
            RecipeType.create("bmnw", "pressing", PressingRecipe.class);


    public static final Supplier<RecipeType<RecipeHolder<AlloyingRecipe>>> ALLOYING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.ALLOYING_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<BuildersSmeltingRecipe>>> BUILDERS_SMELTING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.BUILDERS_SMELTING_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<ShreddingRecipe>>> SHREDDING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.SHREDDING_TYPE);
}
