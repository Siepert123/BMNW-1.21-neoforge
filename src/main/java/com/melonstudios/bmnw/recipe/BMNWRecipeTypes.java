package com.melonstudios.bmnw.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BMNWRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "bmnw");

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyingRecipe>> ALLOYING = RECIPE_TYPES.register(
            "alloying",
            () -> AlloyingRecipe.TYPE
    );

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
