package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.AlloyingRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.ShreddingRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.jei.subtype.FluidContainerSubtype;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.init.BMNWTabs;
import nl.melonstudios.bmnw.hardcoded.recipe.jei.subtype.FireMarbleSubtypeInterpreter;
import nl.melonstudios.bmnw.item.tools.FluidContainerItem;
import nl.melonstudios.bmnw.screen.AlloyFurnaceScreen;
import nl.melonstudios.bmnw.screen.PressScreen;
import nl.melonstudios.bmnw.screen.WorkbenchScreen;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return BMNW.namespace("jei_compat");
    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
        modAliasRegistration.addModAliases("bmnw", "BMNW", "Bunkers, Machines & Nuclear Weapons");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(BMNWRecipeTypes.WORKBENCH, WorkbenchRecipes.instance.recipes);
        registration.addRecipes(BMNWRecipeTypes.PRESSING, PressingRecipes.instance.getJEIRecipeList());
        registration.addRecipes(BMNWRecipeTypes.ALLOYING, AlloyingRecipes.instance.getJEIRecipeList());
        registration.addRecipes(BMNWRecipeTypes.SHREDDING, ShreddingRecipes.instance.getJEIRecipeList());

        registration.addItemStackInfo(
                Arrays.asList(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.parse("bmnw:stamps"))).getItems()),
                Component.literal("Stamps are used to, well, stamp items into a certain shape."),
                Component.literal("You will also need a stamping press of some sort.")
        );

        registration.addIngredientInfo(BMNWItems.URANIUM_SANDWICH, Component.literal("Yummy! ".repeat(100)));

        registration.addItemStackInfo(
                new ItemStack(BMNWItems.FIRE_MARBLE.get()),
                Component.literal("Fire Marbles are found in meteors."),
                Component.literal("They may seem strange, but these orbs are a key to great power!"),
                Component.literal("(This is yet to be added...)")
        );
        registration.addItemStackInfo(
                List.of(
                        BMNWTabs.createFM(0, false),
                        BMNWTabs.createFM(1, false),
                        BMNWTabs.createFM(2, false),
                        BMNWTabs.createFM(3, false),
                        BMNWTabs.createFM(4, false),
                        BMNWTabs.createFM(5, false)
                ),
                Component.literal("By beaming them with special void energy, Fire Marbles will be assigned a random type."),
                Component.literal("(This is yet to be added...)")
        );
        registration.addItemStackInfo(
                List.of(
                        BMNWTabs.createFM(0, true),
                        BMNWTabs.createFM(1, true),
                        BMNWTabs.createFM(2, true),
                        BMNWTabs.createFM(3, true),
                        BMNWTabs.createFM(4, true),
                        BMNWTabs.createFM(5, true)
                ),
                Component.literal("Each Fire Marble type has a random assigned frequency. This frequency differs per seed."),
                Component.literal("A special device can be used to test a frequency on a Fire Marble."),
                Component.literal("The more accurate a frequency is, the brighter the marble will shine!"),
                Component.literal("They can also be extinguished in water."),
                Component.literal("(This is yet to be added...)")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(BMNWRecipeTypes.WORKBENCH,
                BMNWItems.IRON_WORKBENCH,
                BMNWItems.STEEL_WORKBENCH
        );
        registration.addRecipeCatalyst(BMNWItems.PRESS, BMNWRecipeTypes.PRESSING);
        registration.addRecipeCatalyst(BMNWItems.ALLOY_BLAST_FURNACE, BMNWRecipeTypes.ALLOYING);
        registration.addRecipeCatalyst(BMNWItems.LARGE_SHREDDER, BMNWRecipeTypes.SHREDDING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WorkbenchRecipeCategory());
        registration.addRecipeCategories(new PressingRecipeCategory());
        registration.addRecipeCategories(new AlloyingRecipeCategory());
        registration.addRecipeCategories(new ShreddingRecipeCategory());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                WorkbenchScreen.class,
                113, 14, 55, 64,
                BMNWRecipeTypes.WORKBENCH
        );
        registration.addRecipeClickArea(
                PressScreen.class,
                55, 34, 18, 18,
                BMNWRecipeTypes.PRESSING
        );
        registration.addRecipeClickArea(
                PressScreen.class,
                79, 34, 24, 17,
                BMNWRecipeTypes.PRESSING
        );
        registration.addRecipeClickArea(
                AlloyFurnaceScreen.class,
                83, 22, 28, 38,
                BMNWRecipeTypes.ALLOYING
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(BMNWItems.FIRE_MARBLE.get(), new FireMarbleSubtypeInterpreter());
        for (FluidContainerItem item : FluidContainerItem.getAllFluidContainers()) {
            registration.registerSubtypeInterpreter(item, FluidContainerSubtype.interpreter());
        }
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAliases(VanillaTypes.ITEM_STACK, tagItemList("bmnw:stamps"), "mold");
        registration.addAliases(VanillaTypes.ITEM_STACK, itemsToStacks(
                BMNWItems.COMBUSTION_ENGINE
        ), "generator");
    }

    private static Set<ItemStack> itemsToStacks(ItemLike... items) {
        HashSet<ItemStack> stacks = new HashSet<>();
        for (ItemLike item : items) stacks.add(item.asItem().getDefaultInstance());
        return stacks;
    }
    private static List<ItemStack> tagItemList(String tag) {
        return Arrays.asList(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.parse(tag))).getItems());
    }
}
