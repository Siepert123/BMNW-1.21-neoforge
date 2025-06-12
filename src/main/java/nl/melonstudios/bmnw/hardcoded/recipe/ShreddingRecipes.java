package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

//TODO: implement more recipes
public class ShreddingRecipes {
    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }

    public static final ShreddingRecipes instance = new ShreddingRecipes();

    private ShreddingRecipes() {
        this.addRecipe(Ingredient.of(Items.COBBLESTONE), new ItemStack(Items.GRAVEL));
    }

    private final ArrayList<ShreddingRecipe> recipes = new ArrayList<>();

    public ItemStack getResult(ItemStack input) {
        for (ShreddingRecipe recipe : this.recipes) {
            if (recipe.input().test(input)) return recipe.result().copy();
        }
        return new ItemStack(Items.GRAVEL, 1);
    }

    public void addRecipe(ShreddingRecipe recipe) {
        this.recipes.add(recipe);
    }
    public void addRecipe(Ingredient input, ItemStack result) {
        this.addRecipe(new ShreddingRecipe(input, result));
    }
}
