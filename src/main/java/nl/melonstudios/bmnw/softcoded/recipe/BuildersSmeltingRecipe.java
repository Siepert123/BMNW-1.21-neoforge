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
import nl.melonstudios.bmnw.init.BMNWRecipes;

public record BuildersSmeltingRecipe(Ingredient input, ItemStack output, int recipeTime) implements Recipe<BuildersSmeltingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input());
        return list;
    }

    @Override
    public boolean matches(BuildersSmeltingRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        return this.input.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(BuildersSmeltingRecipeInput input, HolderLookup.Provider registries) {
        return this.output().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMNWRecipes.BUILDERS_SMELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.BUILDERS_SMELTING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BuildersSmeltingRecipe> {
        public static final MapCodec<BuildersSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(BuildersSmeltingRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(BuildersSmeltingRecipe::output),
                Codec.INT.fieldOf("time").forGetter(BuildersSmeltingRecipe::recipeTime)
        ).apply(inst, BuildersSmeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BuildersSmeltingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, BuildersSmeltingRecipe::input,
                        ItemStack.STREAM_CODEC, BuildersSmeltingRecipe::output,
                        ByteBufCodecs.INT, BuildersSmeltingRecipe::recipeTime,
                        BuildersSmeltingRecipe::new);

        @Override
        public MapCodec<BuildersSmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BuildersSmeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
