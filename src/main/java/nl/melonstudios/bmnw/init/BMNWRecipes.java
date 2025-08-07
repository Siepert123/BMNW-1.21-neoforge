package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.softcoded.recipe.*;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileFinsCosts;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileFuselageCosts;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileThrusterCosts;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileWarheadCosts;

public class BMNWRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "bmnw");
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "bmnw");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HeaterFuelBonusRecipe>> HEATER_FUEL_BONUS_SERIALIZER =
            SERIALIZERS.register("heater_fuel_bonus", HeaterFuelBonusRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<HeaterFuelBonusRecipe>> HEATER_FUEL_BONUS_TYPE =
            TYPES.register("heater_fuel_bonus", RecipeTypeImpl.create("heater_fuel_bonus"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorkbenchRecipe>> WORKBENCH_SERIALIZER =
            SERIALIZERS.register("workbench", WorkbenchRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<WorkbenchRecipe>> WORKBENCH_TYPE =
            TYPES.register("workbench", RecipeTypeImpl.create("workbench"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PressingRecipe>> PRESSING_SERIALIZER =
            SERIALIZERS.register("pressing", PressingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PressingRecipe>> PRESSING_TYPE =
            TYPES.register("pressing", RecipeTypeImpl.create("pressing"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyingRecipe>> ALLOYING_SERIALIZER =
            SERIALIZERS.register("alloying", AlloyingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyingRecipe>> ALLOYING_TYPE =
            TYPES.register("alloying", RecipeTypeImpl.create("alloying"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BuildersSmeltingRecipe>> BUILDERS_SMELTING_SERIALIZER =
            SERIALIZERS.register("builders_smelting", BuildersSmeltingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<BuildersSmeltingRecipe>> BUILDERS_SMELTING_TYPE =
            TYPES.register("builders_smelting", RecipeTypeImpl.create("builders_smelting"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShreddingRecipe>> SHREDDING_SERIALIZER =
            SERIALIZERS.register("shredding", ShreddingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShreddingRecipe>> SHREDDING_TYPE =
            TYPES.register("shredding", RecipeTypeImpl.create("shredding"));


    //region Missile things

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MissileThrusterCosts>> MISSILE_THRUSTER_SERIALIZER =
            SERIALIZERS.register("missile_thruster", MissileThrusterCosts.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MissileThrusterCosts>> MISSILE_THRUSTER_TYPE =
            TYPES.register("missile_thruster", RecipeTypeImpl.create("missile_thruster"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MissileFinsCosts>> MISSILE_FINS_SERIALIZER =
            SERIALIZERS.register("missile_fins", MissileFinsCosts.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MissileFinsCosts>> MISSILE_FINS_TYPE =
            TYPES.register("missile_fins", RecipeTypeImpl.create("missile_fins"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MissileFuselageCosts>> MISSILE_FUSELAGE_SERIALIZER =
            SERIALIZERS.register("missile_fuselage", MissileFuselageCosts.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MissileFuselageCosts>> MISSILE_FUSELAGE_TYPE =
            TYPES.register("missile_fuselage", RecipeTypeImpl.create("missile_fuselage"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MissileWarheadCosts>> MISSILE_WARHEAD_SERIALIZER =
            SERIALIZERS.register("missile_warhead", MissileWarheadCosts.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MissileWarheadCosts>> MISSILE_WARHEAD_TYPE =
            TYPES.register("missile_warhead", RecipeTypeImpl.create("missile_warhead"));

    //endregion

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
