package nl.melonstudios.bmnw.softcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record BuildersSmeltingRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return this.input();
    }

    @Override
    public int size() {
        return 1;
    }
}
