package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientPair(Ingredient ingredient1, Ingredient ingredient2) {
    public boolean matches(ItemStack stack1, ItemStack stack2) {
        boolean flag1 = ingredient1.test(stack1) && ingredient2.test(stack2);
        if (flag1) return true;
        return ingredient2.test(stack1) && ingredient1.test(stack2);
    }
}
