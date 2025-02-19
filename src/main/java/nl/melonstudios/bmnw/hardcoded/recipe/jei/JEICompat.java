package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.BMNWBlocks;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.item.BMNWItems;

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
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BMNWBlocks.ANTENNA_DISH, BMNWRecipeTypes.PRESSING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PressingRecipeCategory());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

    }
}
