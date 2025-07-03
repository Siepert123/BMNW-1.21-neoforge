package nl.melonstudios.bmnw.softcoded.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.WrappedSingletonRecipeInput;

public record ShreddingRecipe(Ingredient input, ItemStack result) implements Recipe<WrappedSingletonRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.input);
    }

    @Override
    public boolean matches(WrappedSingletonRecipeInput input, Level level) {
        return this.input.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(WrappedSingletonRecipeInput input, HolderLookup.Provider registries) {
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
        return BMNWRecipes.SHREDDING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.SHREDDING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ShreddingRecipe> {
        public static final MapCodec<ShreddingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ShreddingRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(ShreddingRecipe::result)
        ).apply(inst, ShreddingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShreddingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ShreddingRecipe::input,
                ItemStack.STREAM_CODEC, ShreddingRecipe::result,
                ShreddingRecipe::new
        );

        @Override
        public MapCodec<ShreddingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShreddingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
