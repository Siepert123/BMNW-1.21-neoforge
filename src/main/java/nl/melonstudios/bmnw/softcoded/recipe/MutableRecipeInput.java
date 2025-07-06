package nl.melonstudios.bmnw.softcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class MutableRecipeInput implements RecipeInput {
    public ItemStack stack;
    @Override
    public ItemStack getItem(int index) {
        return this.stack;
    }

    @Override
    public int size() {
        return 1;
    }
}
