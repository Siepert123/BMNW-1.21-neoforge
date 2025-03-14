package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record WorkbenchRecipe(ResourceLocation rsl, List<Ingredient> ingredients, ItemStack result, int minTier) {
}
