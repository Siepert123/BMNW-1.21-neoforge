package nl.melonstudios.bmnw.hardcoded.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;

public record WrappedUnorderedDoubleRecipeInput(IItemHandler inventory, int slot1, int slot2) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? this.inventory.getStackInSlot(this.slot1) : this.inventory.getStackInSlot(this.slot2);
    }

    @Override
    public int size() {
        return 2;
    }
}
