package nl.melonstudios.bmnw.softcoded.recipe;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record WorkbenchRecipeInput(Inventory inventory) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return this.inventory.getItem(index);
    }

    @Override
    public int size() {
        return this.inventory.getContainerSize();
    }
}
