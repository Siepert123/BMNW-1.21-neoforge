package nl.melonstudios.bmnw.softcoded.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

/**
 * Simple recipe type implementation to prevent anonymous classes
 * @param <T> The recipe type parameter
 */
public class RecipeTypeImpl<T extends Recipe<?>> implements RecipeType<T> {
    private final String id;
    public RecipeTypeImpl(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public static <T extends Recipe<?>> Supplier<RecipeTypeImpl<T>> create(String id) {
        return () -> new RecipeTypeImpl<>(id);
    }
}
