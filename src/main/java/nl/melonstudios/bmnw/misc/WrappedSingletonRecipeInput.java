package nl.melonstudios.bmnw.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;

public record WrappedSingletonRecipeInput(IItemHandler inventory, int slot) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return this.inventory.getStackInSlot(this.slot);
    }

    @Override
    public int size() {
        return 1;
    }
}
