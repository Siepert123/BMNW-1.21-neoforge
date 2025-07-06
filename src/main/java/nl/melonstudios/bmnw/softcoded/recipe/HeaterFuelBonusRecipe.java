package nl.melonstudios.bmnw.softcoded.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

public record HeaterFuelBonusRecipe(Ingredient input, float durationMultiplier, float heatMultiplier) implements Recipe<MutableRecipeInput> {
    @Override
    public boolean matches(MutableRecipeInput input, Level level) {
        if (level.isClientSide) return false;
        return this.input.test(input.stack);
    }

    @Override
    public ItemStack assemble(MutableRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMNWRecipes.HEATER_FUEL_BONUS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMNWRecipes.HEATER_FUEL_BONUS_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HeaterFuelBonusRecipe> {
        public static final MapCodec<HeaterFuelBonusRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("item")
                        .forGetter(HeaterFuelBonusRecipe::input),
                Codec.FLOAT.fieldOf("duration_multiplier")
                        .orElse(1.0F)
                        .validate((f) -> f <= 0.0F ?
                                DataResult.error(() -> "duration multiplier must be positive", 1.0F) :
                                DataResult.success(f))
                        .forGetter(HeaterFuelBonusRecipe::durationMultiplier),
                Codec.FLOAT.fieldOf("heat_multiplier")
                        .orElse(1.0F)
                        .validate((f) -> f <= 0.0F ?
                                DataResult.error(() -> "heat multiplier must be positive", 1.0F) :
                                DataResult.success(f))
                        .forGetter(HeaterFuelBonusRecipe::durationMultiplier)
        ).apply(inst, HeaterFuelBonusRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HeaterFuelBonusRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, HeaterFuelBonusRecipe::input,
                        ByteBufCodecs.FLOAT, HeaterFuelBonusRecipe::durationMultiplier,
                        ByteBufCodecs.FLOAT, HeaterFuelBonusRecipe::heatMultiplier,
                        HeaterFuelBonusRecipe::new);

        @Override
        public MapCodec<HeaterFuelBonusRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HeaterFuelBonusRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
