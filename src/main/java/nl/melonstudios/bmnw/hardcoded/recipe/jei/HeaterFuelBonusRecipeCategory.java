package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.softcoded.recipe.HeaterFuelBonusRecipe;
import org.jetbrains.annotations.Nullable;

public class HeaterFuelBonusRecipeCategory implements IRecipeCategory<RecipeHolder<HeaterFuelBonusRecipe>> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/jei/heater_fuel_bonus.png");

    private final IDrawable icon, background;
    public HeaterFuelBonusRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BMNWItems.INDUSTRIAL_HEATER.get()));
        this.background = guiHelper.drawableBuilder(GUI_TEXTURE, 0, 0, 128, 64)
                .setTextureSize(128, 64).build();
    }

    @Override
    public RecipeType<RecipeHolder<HeaterFuelBonusRecipe>> getRecipeType() {
        return BMNWRecipeTypes.HEATER_FUEL_BONUS.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.bmnw.heater_fuel_bonus");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HeaterFuelBonusRecipe> recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 1).addIngredients(recipe.value().input());
    }

    @Override
    public void draw(RecipeHolder<HeaterFuelBonusRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        this.background.draw(guiGraphics);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                Component.translatable("recipe.bmnw.heater_fuel_bonus.duration"), 64, 20, 0xFFFFFFFF);
        float pct = (recipe.value().durationMultiplier()-1.0F) * 100;
        String suffix = pct < 0 ? "" : "+";
        int color = pct < 0 ? 0xFF0000 : (pct > 0 ? 0x00FF00 : 0xFFFFFF);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                Component.literal(suffix + pct + "%"), 64, 29, color);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                Component.translatable("recipe.bmnw.heater_fuel_bonus.heat"), 64, 44, 0xFFFFFFFF);
        pct = (recipe.value().heatMultiplier()-1.0F) * 100;
        suffix = pct < 0 ? "" : "+";
        color = pct < 0 ? 0xFF0000 : (pct > 0 ? 0x00FF00 : 0xFFFFFF);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font,
                Component.literal(suffix + pct + "%"), 64, 53, color);
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 64;
    }
}
