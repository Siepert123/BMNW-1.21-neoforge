package nl.melonstudios.bmnw.softcoded.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWRecipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record WorkbenchRecipe(List<Ingredient> ingredients, int requiredTier, ItemStack result, String group) implements Recipe<WorkbenchRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    @Deprecated
    @Override
    public boolean matches(WorkbenchRecipeInput input, Level level) {
        if (level.isClientSide) return false;
        List<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : input.inventory().items) {
            copy.add(stack.copy());
        }
        List<Ingredient> ingredients = new ArrayList<>(this.ingredients);
        Iterator<Ingredient> iterator = this.ingredients.iterator();
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

    public static boolean matches(WorkbenchRecipe recipe, Inventory playerInv) {
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
    public static boolean perform(WorkbenchRecipe recipe, Inventory playerInv) {
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

    @Override
    public ItemStack assemble(WorkbenchRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMNWRecipes.WORKBENCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.WORKBENCH_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<WorkbenchRecipe> {
        public static final MapCodec<WorkbenchRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(WorkbenchRecipe::ingredients),
                Codec.INT.fieldOf("requiredTier")
                        .orElse(0)
                        .validate((i) -> i < 0 ?
                        DataResult.error(() -> "Invalid required tier: " + i, 0) :
                                DataResult.success(i))
                        .forGetter(WorkbenchRecipe::requiredTier),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(WorkbenchRecipe::result),
                Codec.STRING.fieldOf("group")
                        .orElse("")
                        .validate((group) -> group.length() > 16 ?
                                DataResult.error(() -> "Recipe group too long: max len 16, group len " + group.length(), group.substring(0, 16)) :
                                DataResult.success(group))
                        .forGetter(WorkbenchRecipe::group)
        ).apply(inst, WorkbenchRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNet, Serializer::fromNet
        );

        private static WorkbenchRecipe fromNet(RegistryFriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            ingredients.replaceAll(previous -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            int requiredTier = buf.readVarInt();
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            String group = buf.readUtf(16);
            return new WorkbenchRecipe(ingredients, requiredTier, result, group);
        }

        private static void toNet(RegistryFriendlyByteBuf buf, WorkbenchRecipe recipe) {
            buf.writeInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            buf.writeVarInt(recipe.requiredTier);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeUtf(recipe.group, 16);
        }

        @Override
        public MapCodec<WorkbenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
