package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.softcoded.recipe.AlloyingRecipe;
import org.jetbrains.annotations.Nullable;

public class AlloyingRecipeCategory implements IRecipeCategory<RecipeHolder<AlloyingRecipe>> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/alloy_furnace/gui.png");

    private final IDrawable icon, background;

    public AlloyingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BMNWItems.ALLOY_BLAST_FURNACE.get()));
        this.background = guiHelper.createDrawable(GUI_TEXTURE, 65, 15, 74, 52);
    }

    @Override
    public RecipeType<RecipeHolder<AlloyingRecipe>> getRecipeType() {
        return BMNWRecipeTypes.ALLOYING.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.alloy_blast_furnace");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloyingRecipe> recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 1).addIngredients(recipe.value().input1());
        builder.addInputSlot(1, 35).addIngredients(recipe.value().input2());
        builder.addOutputSlot(53, 18).addItemStack(recipe.value().result());
    }

    @Override
    public void draw(RecipeHolder<AlloyingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 74;
    }
    @Override
    public int getHeight() {
        return 52;
    }
}
