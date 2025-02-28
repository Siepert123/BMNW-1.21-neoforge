package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import nl.melonstudios.bmnw.hardcoded.recipe.jei.PressingRecipe;
import nl.melonstudios.bmnw.init.BMNWItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class PressingRecipes {
    private static final Logger LOGGER = LogManager.getLogger("BMNW Pressing Recipes");
    private static final boolean debug = true;
    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }
    public enum MoldType {
        BLANK(tag("bmnw:molds/blank")),
        PLATE(tag("bmnw:molds/plate")),
        WIRE(tag("bmnw:molds/wire"));

        private final TagKey<Item> itemTag;
        MoldType(TagKey<Item> itemTag) {
            this.itemTag = itemTag;
        }

        public boolean isItemThis(ItemStack stack) {
            return stack.is(itemTag);
        }

        @Nullable
        public static MoldType getMoldType(ItemStack item) {
            if (debug) return MoldType.PLATE;
            if (item.isEmpty()) return null;
            for (MoldType type : values()) {
                if (type.isItemThis(item)) return type;
            }
            return null;
        }
    }
    public static final PressingRecipes instance = new PressingRecipes();
    private PressingRecipes() {
        this.recipes.put(MoldType.BLANK, new HashMap<>());
        this.recipes.put(MoldType.PLATE, new HashMap<>());
        this.recipes.put(MoldType.WIRE, new HashMap<>());

        addRecipe(MoldType.PLATE, "c:ingots/iron", BMNWItems.IRON_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/copper", BMNWItems.COPPER_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/gold", BMNWItems.GOLD_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/conductive_copper", BMNWItems.CONDUCTIVE_COPPER_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/lead", BMNWItems.LEAD_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/aluminium", BMNWItems.ALUMINIUM_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/tungsten", BMNWItems.TUNGSTEN_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/titanium", BMNWItems.TITANIUM_PLATE);
        addRecipe(MoldType.PLATE, "c:ingots/steel", BMNWItems.STEEL_PLATE);

        addRecipe(MoldType.WIRE, "c:ingots/iron", BMNWItems.IRON_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/copper", BMNWItems.COPPER_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/gold", BMNWItems.GOLD_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/conductive_copper", BMNWItems.CONDUCTIVE_COPPER_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/lead", BMNWItems.LEAD_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/aluminium", BMNWItems.ALUMINIUM_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/tungsten", BMNWItems.TUNGSTEN_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/titanium", BMNWItems.TITANIUM_WIRE.toStack(8));
        addRecipe(MoldType.WIRE, "c:ingots/steel", BMNWItems.STEEL_WIRE.toStack(8));
    }

    private final Map<MoldType, Map<Ingredient, ItemStack>> recipes = new HashMap<>();

    public boolean canBeUsedAsMold(ItemStack item) {
        if (item.isEmpty()) return false;
        for (MoldType type : MoldType.values()) {
            if (type.isItemThis(item)) return true;
        }
        return false;
    }

    public ItemStack getResult(ItemStack mold, ItemStack input) {
        MoldType type = MoldType.getMoldType(mold);
        if (type != null) {
            Map<Ingredient, ItemStack> recipes = this.recipes.get(type);
            if (recipes == null) return ItemStack.EMPTY;
            for (Map.Entry<Ingredient, ItemStack> entry : recipes.entrySet()) {
                if (entry.getKey().test(input)) return entry.getValue();
            }
        }
        return ItemStack.EMPTY;
    }

    public void addRecipe(MoldType moldType, Ingredient input, ItemStack result) {
        Map<Ingredient, ItemStack> map = this.recipes.get(moldType);
        map.putIfAbsent(input, result);
    }
    public void addRecipe(MoldType moldType, String tag, ItemStack result) {
        this.addRecipe(moldType, Ingredient.of(tag(tag)), result);
    }
    public void addRecipe(MoldType moldType, Ingredient input, ItemLike result) {
        this.addRecipe(moldType, input, result.asItem().getDefaultInstance());
    }
    public void addRecipe(MoldType moldType, String tag, ItemLike result) {
        this.addRecipe(moldType, tag, result.asItem().getDefaultInstance());
    }

    public int recipeCount(MoldType moldType) {
        return this.recipes.get(moldType).size();
    }

    public List<PressingRecipe> getJEIRecipeList() {
        List<PressingRecipe> list = new ArrayList<>();
        for (Map.Entry<MoldType, Map<Ingredient, ItemStack>> entry : this.recipes.entrySet()) {
            MoldType moldType = entry.getKey();
            for (Map.Entry<Ingredient, ItemStack> internalEntry : entry.getValue().entrySet()) {
                list.add(new PressingRecipe(moldType, internalEntry.getKey(), internalEntry.getValue()));
            }
        }
        return list;
    }
}
