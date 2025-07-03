package nl.melonstudios.bmnw.softcoded.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.hardcoded.recipe.PressRecipeInput;
import nl.melonstudios.bmnw.hardcoded.recipe.StampType;
import nl.melonstudios.bmnw.init.BMNWRecipes;

public record PressingRecipe(Ingredient input, StampType stamp, ItemStack result) implements Recipe<PressRecipeInput> {
    @Override
    public boolean matches(PressRecipeInput input, Level level) {
        return this.stamp.isItemThis(input.getItem(0)) && this.input.test(input.getItem(1));
    }

    @Override
    public ItemStack assemble(PressRecipeInput input, HolderLookup.Provider registries) {
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
        return BMNWRecipes.PRESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.PRESSING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe> {
        public static final MapCodec<PressingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(PressingRecipe::input),
                StampType.CODEC.fieldOf("stamp").forGetter(PressingRecipe::stamp),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(PressingRecipe::result)
        ).apply(inst, PressingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PressingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, PressingRecipe::input,
                StampType.STREAM_CODEC, PressingRecipe::stamp,
                ItemStack.STREAM_CODEC, PressingRecipe::result,
                PressingRecipe::new
        );

        @Override
        public MapCodec<PressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PressingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
