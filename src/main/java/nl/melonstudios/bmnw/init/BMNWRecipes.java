package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;
import nl.melonstudios.bmnw.softcoded.recipe.RecipeTypeImpl;

public class BMNWRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "bmnw");
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "bmnw");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BuildersSmeltingRecipe>> BUILDERS_SMELTING_SERIALIZER =
            SERIALIZERS.register("builders_smelting", BuildersSmeltingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<BuildersSmeltingRecipe>> BUILDERS_SMELTING_TYPE =
            TYPES.register("builders_smelting", RecipeTypeImpl.create("builders_smelting"));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
