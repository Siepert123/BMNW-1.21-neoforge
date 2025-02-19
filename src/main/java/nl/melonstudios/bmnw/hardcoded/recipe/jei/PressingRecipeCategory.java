package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class PressingRecipeCategory implements IRecipeCategory<PressingRecipe> {
    @Override
    public RecipeType<PressingRecipe> getRecipeType() {
        return BMNWRecipeTypes.PRESSING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.pressing");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PressingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot();
        builder.addInputSlot();
        builder.addOutputSlot();
    }
}
