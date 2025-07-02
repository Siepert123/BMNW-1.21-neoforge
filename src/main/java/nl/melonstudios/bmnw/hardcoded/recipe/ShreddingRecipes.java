package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.init.BMNWItems;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShreddingRecipes implements ResourceManagerReloadListener {
    private static final Logger LOGGER = IngredientPair.LOGGER;
    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }

    public static final ShreddingRecipes instance = new ShreddingRecipes();

    private ShreddingRecipes() {
        this.reload();
    }

    public void reload() {
        LOGGER.debug("Reloading shredding recipes");
        this.recipes.clear();
        this.addRecipe(Ingredient.of(Items.COBBLESTONE), new ItemStack(Items.GRAVEL));
        this.addRecipe(Ingredient.of(Items.GRAVEL), new ItemStack(Items.SAND));
        this.addRecipe(Ingredient.of(tag("c:sands")), new ItemStack(BMNWItems.DUST.get()));
        this.detectMetals().forEach(this::rawOreToDust);
    }

    private ArrayList<String> detectMetals() {
        ArrayList<String> list = new ArrayList<>();
        for (ItemStack stack : Ingredient.of(tag("c:ingots")).getItems()) {
            ResourceLocation rsl = stack.getItem().builtInRegistryHolder().key().location();
            if (rsl.getPath().endsWith("_ingot")) {
                list.add(rsl.getPath().substring(0, rsl.getPath().length() - "_ingot".length()));
            } else if (rsl.getPath().startsWith("ingot_")) {
                list.add(rsl.getPath().substring("ingot_".length()));
            }
        }
        return list;
    }

    private void rawOreToDust(String name) {
        Ingredient ingot = Ingredient.of(tag("c:ingots/" + name));
        Ingredient rawOre = Ingredient.of(tag("c:raw_materials/" + name));
        Ingredient ore = Ingredient.of(tag("c:ores/" + name));
        Ingredient crystal = Ingredient.of(tag("bmnw:crystals/" + name));
        Ingredient dust = Ingredient.of(tag("c:dusts/" + name));
        if (!this.validateIngredient(dust)) return;
        if (this.validateIngredient(ingot)) {
            this.addRecipe(ingot, dust.getItems()[0]);
        }
        if (this.validateIngredient(rawOre)) {
            ItemStack stack = dust.getItems()[0].copy();
            stack.setCount(2);
            this.addRecipe(rawOre, stack);
        }
        if (this.validateIngredient(ore)) {
            ItemStack stack = dust.getItems()[0].copy();
            stack.setCount(2);
            this.addRecipe(ore, stack);
        }
        if (this.validateIngredient(crystal)) {
            ItemStack stack = dust.getItems()[0].copy();
            stack.setCount(3);
            this.addRecipe(crystal, stack);
        }
    }

    private boolean validateIngredient(Ingredient ingredient) {
        return ingredient.getItems().length != 0 && ingredient.getItems()[0].getItem() != Items.BARRIER;
    }

    private final ArrayList<ShreddingRecipe> recipes = new ArrayList<>();

    public ItemStack getResult(ItemStack input) {
        for (ShreddingRecipe recipe : this.recipes) {
            if (recipe.input().test(input)) return recipe.result().copy();
        }
        return new ItemStack(BMNWItems.DUST.get(), 1);
    }

    public void addRecipe(ShreddingRecipe recipe) {
        this.recipes.add(recipe);
    }
    public void addRecipe(Ingredient input, ItemStack result) {
        this.addRecipe(new ShreddingRecipe(input, result));
    }

    public List<ShreddingRecipe> getJEIRecipeList() {
        return this.recipes;
    }

    @Override
    public String getName() {
        return "ShreddingRecipes";
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.reload();
    }
}
