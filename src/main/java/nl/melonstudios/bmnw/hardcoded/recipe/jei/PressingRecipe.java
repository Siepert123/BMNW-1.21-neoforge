package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;

public record PressingRecipe(PressingRecipes.MoldType moldType, Ingredient input, ItemStack result) {
}
