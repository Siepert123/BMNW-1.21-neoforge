package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.AlloyingRecipes;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.item.subtype.FireMarbleSubtypeInterpreter;
import nl.melonstudios.bmnw.screen.AlloyFurnaceScreen;
import nl.melonstudios.bmnw.screen.PressScreen;

import javax.annotation.Nonnull;

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
        registration.addRecipes(BMNWRecipeTypes.PRESSING, PressingRecipes.instance.getJEIRecipeList());
        registration.addRecipes(BMNWRecipeTypes.ALLOYING, AlloyingRecipes.instance.getJEIRecipeList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BMNWItems.PRESS, BMNWRecipeTypes.PRESSING);
        registration.addRecipeCatalyst(BMNWItems.ALLOY_BLAST_FURNACE, BMNWRecipeTypes.ALLOYING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PressingRecipeCategory());
        registration.addRecipeCategories(new AlloyingRecipeCategory());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
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
                BMNWRecipeTypes.PRESSING
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(BMNWItems.FIRE_MARBLE.get(), new FireMarbleSubtypeInterpreter());
    }
}
