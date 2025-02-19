package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;

public class PressingRecipe {
    public final PressingRecipes.MoldType moldType;
    public final Ingredient input;
    public final ItemStack result;

    public PressingRecipe(PressingRecipes.MoldType moldType, Ingredient input, ItemStack result) {
        this.moldType = moldType;
        this.input = input;
        this.result = result;
    }
}
