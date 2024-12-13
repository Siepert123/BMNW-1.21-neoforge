package com.siepert.bmnw.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

//big thanks to emma for letting me copy code :3
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record AlloyingRecipe(int duration, Ingredient input1, Ingredient input2, ItemStack result) implements Recipe<RecipeInput> {
    public static final RecipeType<AlloyingRecipe> TYPE = RecipeType.simple(ResourceLocation.parse("bmnw:alloying"));
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean matches(RecipeInput input, Level level) {
        boolean flag1 = false;
        boolean flag2 = false;

        if (input1.test(input.getItem(0)) && input2.test(input.getItem(1))) flag1 = true;
        if (input2.test(input.getItem(0)) && input1.test(input.getItem(1))) flag2 = true;

        return flag1 || flag2;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
        public static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                Codec.INT.fieldOf("duration").forGetter(AlloyingRecipe::duration),
                Ingredient.CODEC.fieldOf("input1").forGetter(AlloyingRecipe::input1),
                Ingredient.CODEC.fieldOf("input2").forGetter(AlloyingRecipe::input2),
                ItemStack.CODEC.fieldOf("result").forGetter(AlloyingRecipe::result)
        ).apply(it, AlloyingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

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
