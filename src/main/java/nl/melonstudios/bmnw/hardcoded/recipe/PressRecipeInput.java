package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;

public record PressRecipeInput(IItemHandler inventory, int stampSlot, int inputSlot) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? this.inventory.getStackInSlot(this.stampSlot) : this.inventory.getStackInSlot(this.inputSlot);
    }

    @Override
    public int size() {
        return 2;
    }
}
