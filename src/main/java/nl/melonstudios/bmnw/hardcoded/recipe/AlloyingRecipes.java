package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.jei.AlloyingRecipe;
import nl.melonstudios.bmnw.init.BMNWItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlloyingRecipes {
    private static TagKey<Item> tag(String key) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(key));
    }
    public static final AlloyingRecipes instance = new AlloyingRecipes();

    private AlloyingRecipes() {
        this.addRecipe(
                new IngredientPair(Ingredient.of(tag("c:ingots/iron")), Ingredient.of(Items.COAL)),
                new ItemStack(BMNWItems.STEEL_INGOT.get()),
                BMNW.namespace("alloying/steel")
        );
        this.addRecipe(
                new IngredientPair(Ingredient.of(tag("c:raw_materials/iron")), Ingredient.of(Items.COAL)),
                new ItemStack(BMNWItems.STEEL_INGOT.get(), 2),
                BMNW.namespace("alloying/steel_from_raw_iron")
        );
        this.addRecipe(
                new IngredientPair(Ingredient.of(tag("c:ingots/copper")), Ingredient.of(tag("c:dusts/redstone"))),
                new ItemStack(BMNWItems.CONDUCTIVE_COPPER_INGOT.get(), 2),
                BMNW.namespace("alloying/conductive_copper")
        );
    }

    private final Map<IngredientPair, ItemStack> recipes = new HashMap<>();
    private final List<ResourceLocation> registryNames = new ArrayList<>();

    public ItemStack getResult(ItemStack input1, ItemStack input2) {
        if (input1.isEmpty() || input2.isEmpty()) return ItemStack.EMPTY;
        for (Map.Entry<IngredientPair, ItemStack> entry : this.recipes.entrySet()) {
            if (entry.getKey().matches(input1, input2)) return entry.getValue().copy();
        }
        return ItemStack.EMPTY;
    }

    public void addRecipe(IngredientPair pair, ItemStack result, ResourceLocation rsl) {
        this.recipes.put(pair, result);
        this.registryNames.add(rsl);
    }

    public List<AlloyingRecipe> getJEIRecipeList() {
        List<AlloyingRecipe> list = new ArrayList<>();
        Map.Entry<IngredientPair, ItemStack>[] entries = this.recipes.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < this.recipes.size(); i++) {
            Map.Entry<IngredientPair, ItemStack> entry = entries[i];
            ResourceLocation rsl = this.registryNames.get(i);
            list.add(new AlloyingRecipe(entry.getKey().ingredient1(), entry.getKey().ingredient2(), entry.getValue(), rsl));
        }
        return list;
    }
}
