package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.item.tools.FluidContainerItem;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluidContainerExchangeCategory implements IRecipeCategory<FluidContainerExchange> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/jei/fluid_container_exchange.png");
    private static final int W = 64;
    private static final int H = 32;


    private final IDrawable icon;
    private final IDrawable background;

    public FluidContainerExchangeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(FluidContainerExchange.filledFluid(BMNWItems.PORTABLE_FLUID_TANK, Fluids.WATER));
        this.background = guiHelper.drawableBuilder(GUI_TEXTURE, 0, 0, W, H).setTextureSize(W,  H).build();
    }

    @Override
    public RecipeType<FluidContainerExchange> getRecipeType() {
        return BMNWRecipeTypes.FLUID_CONTAINER_EXCHANGE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.fluid_container_exchange");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidContainerExchange recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 1, 8).addFluidStack(recipe.fluid(), recipe.fill());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 47, 8).addItemStack(recipe.container());
    }

    @Override
    public void draw(FluidContainerExchange recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return W;
    }

    @Override
    public int getHeight() {
        return H;
    }

    public static List<FluidContainerExchange> collectRecipes() {
        ArrayList<FluidContainerExchange> list = new ArrayList<>();

        for (Fluid fluid : Library.wrapIterator(BuiltInRegistries.FLUID.iterator())) {
            if (!fluid.isSource(fluid.defaultFluidState())) continue;
            for (FluidContainerItem item : FluidContainerItem.getAllFluidContainers()) {
                list.add(FluidContainerExchange.create(item, fluid));
            }
        }

        return list;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(FluidContainerExchange recipe) {
        ResourceLocation rslFluid = BuiltInRegistries.FLUID.getKey(recipe.fluid());
        ResourceLocation rslItem = BuiltInRegistries.ITEM.getKey(recipe.container().getItem());
        return ResourceLocation.fromNamespaceAndPath(rslFluid.getNamespace(),
                rslFluid.getPath() + "_and_" + rslItem.getNamespace() + "_" + rslItem.getPath());
    }
}
