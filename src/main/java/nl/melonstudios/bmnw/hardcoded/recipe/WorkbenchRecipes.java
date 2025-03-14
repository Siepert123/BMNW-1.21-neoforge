package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.init.BMNWTags;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.*;

public class WorkbenchRecipes {
    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }
    public static final WorkbenchRecipes instance = new WorkbenchRecipes();

    private static int maxTier = 1;
    public static int maxTier() {
        return maxTier;
    }

    private WorkbenchRecipes() {
        this.addRecipe("bmnw:workbench/alloy_blast_furnace", 0,
                new ItemStack(BMNWItems.ALLOY_BLAST_FURNACE.get()),
                Ingredient.of(tag("c:plates/iron")),
                Ingredient.of(tag("c:plates/iron")),
                Ingredient.of(tag("c:plates/iron")),
                Ingredient.of(tag("c:bricks/nether")),
                Ingredient.of(tag("c:bricks/nether")),
                Ingredient.of(tag("c:bricks/nether")),
                Ingredient.of(tag("c:bricks/nether"))
        );
    }

    public final List<WorkbenchRecipe> recipes = new ArrayList<>();
    public final Map<ResourceLocation, WorkbenchRecipe> idMap = new HashMap<>();
    public final Map<Integer, List<WorkbenchRecipe>> tierMap = new HashMap<>();

    public void initializeTierMap() {
        this.tierMap.clear();
        for (WorkbenchRecipe recipe : this.recipes) {
            for (int i = recipe.minTier(); i <= maxTier; i++) {
                this.tierMap.putIfAbsent(i, new ArrayList<>());
                this.tierMap.get(i).add(recipe);
            }
        }
    }

    public void addRecipe(String id, int minTier, ItemStack result, Ingredient... ingredients) {
        this.addRecipe(id, Arrays.asList(ingredients), result, minTier);
    }
    public void addRecipe(String id, List<Ingredient> ingredients, ItemStack result, int minTier) {
        maxTier = Math.max(minTier, maxTier());
        ResourceLocation rsl = ResourceLocation.parse(id);
        WorkbenchRecipe recipe = new WorkbenchRecipe(rsl, ingredients, result, minTier);
        this.recipes.add(recipe);
        this.idMap.put(rsl, recipe);
    }

    @Nullable
    @Contract(pure = true)
    public WorkbenchRecipe getRecipeOfIdx(int idx) {
        if (idx < 0 || this.recipes.size() <= idx) return null;
        return this.recipes.get(idx);
    }

    public boolean matches(WorkbenchRecipe recipe, Inventory playerInv) {
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : playerInv.items) {
            copy.add(stack.copy());
        }
        List<Ingredient> ingredients = new ArrayList<>(recipe.ingredients());
        Iterator<Ingredient> iterator = recipe.ingredients().iterator();
        loop:
        while (iterator.hasNext()) {
            Ingredient ingredient = iterator.next();
            for (ItemStack stack : copy) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    ingredients.remove(ingredient);
                    continue loop;
                }
            }
        }
        return ingredients.isEmpty();
    }
    public boolean perform(WorkbenchRecipe recipe, Inventory playerInv) {
        List<Ingredient> copy = new ArrayList<>(recipe.ingredients());
        Iterator<Ingredient> iterator = recipe.ingredients().iterator();
        loop:
        while (iterator.hasNext()) {
            Ingredient ingredient = iterator.next();
            for (ItemStack stack : playerInv.items) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    copy.remove(ingredient);
                    continue loop;
                }
            }
        }
        return copy.isEmpty();
    }
}
