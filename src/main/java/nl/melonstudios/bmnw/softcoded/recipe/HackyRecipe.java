package nl.melonstudios.bmnw.softcoded.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public interface HackyRecipe extends Recipe<EmptyRecipeInput> {
    @Override
    default boolean matches(EmptyRecipeInput input, Level level) {
        return false;
    }
    @Override
    default ItemStack assemble(EmptyRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return false;
    }
    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
}
