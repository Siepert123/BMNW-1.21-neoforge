package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.hardcoded.recipe.ShreddingRecipe;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;

import java.util.function.Supplier;

public class BMNWRecipeTypes {
    public static final RecipeType<WorkbenchRecipe> WORKBENCH =
            RecipeType.create("bmnw", "workbench", WorkbenchRecipe.class);
    public static final RecipeType<PressingRecipe> PRESSING =
            RecipeType.create("bmnw", "pressing", PressingRecipe.class);
    public static final RecipeType<AlloyingRecipe> ALLOYING =
            RecipeType.create("bmnw", "alloying", AlloyingRecipe.class);
    public static final RecipeType<ShreddingRecipe> SHREDDING =
            RecipeType.create("bmnw", "shredding", ShreddingRecipe.class);

    public static final Supplier<RecipeType<RecipeHolder<BuildersSmeltingRecipe>>> BUILDERS_SMELTING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.BUILDERS_SMELTING_TYPE);
}
