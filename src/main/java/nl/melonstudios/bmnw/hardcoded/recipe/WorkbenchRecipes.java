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
        this.addRecipe("bmnw:workbench/iron_workbench", 0,
                new ItemStack(BMNWItems.IRON_WORKBENCH.get()),
                Ingredient.of(tag("c:player_workstations/crafting_tables")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron"))
        );
        this.addRecipe("bmnw:workbench/steel_workbench", 0,
                new ItemStack(BMNWItems.STEEL_WORKBENCH.get()),
                Ingredient.of(BMNWItems.IRON_WORKBENCH.get()),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel"))
        );

        this.addRecipe("bmnw:workbench/blank_iron_stamp", 0,
                new ItemStack(BMNWItems.BLANK_IRON_STAMP.get()),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron")),
                Ingredient.of(tag("c:ingots/iron"))
        );
        this.addRecipe("bmnw:workbench/iron_plate_stamp", 0,
                new ItemStack(BMNWItems.IRON_PLATE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_IRON_STAMP.get())));
        this.addRecipe("bmnw:workbench/iron_wire_stamp", 0,
                new ItemStack(BMNWItems.IRON_WIRE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_IRON_STAMP.get())));

        this.addRecipe("bmnw:workbench/blank_steel_stamp", 0,
                new ItemStack(BMNWItems.BLANK_STEEL_STAMP.get()),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel")),
                Ingredient.of(tag("c:ingots/steel"))
        );
        this.addRecipe("bmnw:workbench/steel_plate_stamp", 0,
                new ItemStack(BMNWItems.STEEL_PLATE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_STEEL_STAMP.get())));
        this.addRecipe("bmnw:workbench/steel_wire_stamp", 0,
                new ItemStack(BMNWItems.STEEL_WIRE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_STEEL_STAMP.get())));

        this.addRecipe("bmnw:workbench/blank_titanium_stamp", 0,
                new ItemStack(BMNWItems.BLANK_TITANIUM_STAMP.get()),
                Ingredient.of(tag("c:ingots/titanium")),
                Ingredient.of(tag("c:ingots/titanium")),
                Ingredient.of(tag("c:ingots/titanium"))
        );
        this.addRecipe("bmnw:workbench/titanium_plate_stamp", 0,
                new ItemStack(BMNWItems.TITANIUM_PLATE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_TITANIUM_STAMP.get())));
        this.addRecipe("bmnw:workbench/titanium_wire_stamp", 0,
                new ItemStack(BMNWItems.TITANIUM_WIRE_STAMP.get()),
                Ingredient.of(new ItemStack(BMNWItems.BLANK_TITANIUM_STAMP.get())));

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

        this.addRecipe("bmnw:workbench/steel_deco_block", 0,
                new ItemStack(BMNWItems.STEEL_DECO_BLOCK.get()),
                Ingredient.of(tag("c:ingots/steel"))
        );
        this.addRecipe("bmnw:workbench/lead_deco_block", 0,
                new ItemStack(BMNWItems.LEAD_DECO_BLOCK.get()),
                Ingredient.of(tag("c:ingots/lead"))
        );
        this.addRecipe("bmnw:workbench/tungsten_deco_block", 0,
                new ItemStack(BMNWItems.TUNGSTEN_DECO_BLOCK.get()),
                Ingredient.of(tag("c:ingots/tungsten"))
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
