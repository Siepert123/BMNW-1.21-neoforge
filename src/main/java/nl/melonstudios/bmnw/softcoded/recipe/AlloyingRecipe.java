package nl.melonstudios.bmnw.softcoded.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.hardcoded.recipe.WrappedUnorderedDoubleRecipeInput;
import nl.melonstudios.bmnw.init.BMNWRecipes;

public record AlloyingRecipe(Ingredient input1, Ingredient input2, ItemStack result, int recipeTime) implements Recipe<WrappedUnorderedDoubleRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.input1, this.input2);
    }

    @Override
    public boolean matches(WrappedUnorderedDoubleRecipeInput input, Level level) {
        if (this.input1.test(input.getItem(0)) && this.input2.test(input.getItem(1))) return true;
        return this.input1.test(input.getItem(1)) && this.input2.test(input.getItem(0));

    }

    @Override
    public ItemStack assemble(WrappedUnorderedDoubleRecipeInput input, HolderLookup.Provider registries) {
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
        return BMNWRecipes.ALLOYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.ALLOYING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
        public static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input1").forGetter(AlloyingRecipe::input1),
                Ingredient.CODEC_NONEMPTY.fieldOf("input2").forGetter(AlloyingRecipe::input2),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(AlloyingRecipe::result),
                Codec.INT.fieldOf("recipeTime").forGetter(AlloyingRecipe::recipeTime)
        ).apply(inst, AlloyingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, AlloyingRecipe::input1,
                Ingredient.CONTENTS_STREAM_CODEC, AlloyingRecipe::input2,
                ItemStack.STREAM_CODEC, AlloyingRecipe::result,
                ByteBufCodecs.INT, AlloyingRecipe::recipeTime,
                AlloyingRecipe::new
        );

        @Override
        public MapCodec<AlloyingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
