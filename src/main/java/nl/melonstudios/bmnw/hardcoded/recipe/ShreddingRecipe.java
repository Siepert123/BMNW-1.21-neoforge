package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ShreddingRecipe(Ingredient input, ItemStack result) {
}
