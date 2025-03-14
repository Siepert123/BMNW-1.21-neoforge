package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record AlloyingRecipe(Ingredient input1, Ingredient input2, ItemStack result, ResourceLocation rsl) {
}
