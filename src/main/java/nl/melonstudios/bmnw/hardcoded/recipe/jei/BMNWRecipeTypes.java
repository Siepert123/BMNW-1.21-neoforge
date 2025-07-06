package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.softcoded.recipe.*;

import java.util.function.Supplier;

public class BMNWRecipeTypes {
    public static final RecipeType<FluidContainerExchange> FLUID_CONTAINER_EXCHANGE =
            RecipeType.create("bmnw", "fluid_container_exchange", FluidContainerExchange.class);

    public static final Supplier<RecipeType<RecipeHolder<HeaterFuelBonusRecipe>>> HEATER_FUEL_BONUS =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.HEATER_FUEL_BONUS_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<WorkbenchRecipe>>> WORKBENCH =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.WORKBENCH_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<PressingRecipe>>> PRESSING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.PRESSING_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<AlloyingRecipe>>> ALLOYING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.ALLOYING_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<BuildersSmeltingRecipe>>> BUILDERS_SMELTING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.BUILDERS_SMELTING_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<ShreddingRecipe>>> SHREDDING =
            RecipeType.createFromDeferredVanilla(BMNWRecipes.SHREDDING_TYPE);
}
