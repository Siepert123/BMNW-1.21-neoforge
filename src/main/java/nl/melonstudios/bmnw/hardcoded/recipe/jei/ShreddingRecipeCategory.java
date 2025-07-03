package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.softcoded.recipe.ShreddingRecipe;
import org.jetbrains.annotations.Nullable;

public class ShreddingRecipeCategory implements IRecipeCategory<RecipeHolder<ShreddingRecipe>> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/jei/shredder.png");

    private final IDrawable icon, background;

    public ShreddingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(GUI_TEXTURE, 0, 0, 54, 18).setTextureSize(54, 18).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BMNWItems.LARGE_SHREDDER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<ShreddingRecipe>> getRecipeType() {
        return BMNWRecipeTypes.SHREDDING.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.shredding");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ShreddingRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(recipe.value().input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 1)
                .addItemStack(recipe.value().result());
    }

    @Override
    public void draw(RecipeHolder<ShreddingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        this.background.draw(guiGraphics);
    }

    @Override
    public int getWidth() {
        return 54;
    }

    @Override
    public int getHeight() {
        return 18;
    }
}
