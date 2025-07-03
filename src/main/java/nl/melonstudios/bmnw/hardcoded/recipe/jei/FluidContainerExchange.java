package nl.melonstudios.bmnw.hardcoded.recipe.jei;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import nl.melonstudios.bmnw.misc.Library;

import java.util.function.Consumer;

public record FluidContainerExchange(ItemStack container, Fluid fluid, int fill) {
    public static FluidContainerExchange create(ItemLike item, Fluid fluid) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) throw new IllegalStateException("Cannot fill item without fluid handler!");
        int cap = handler.getTankCapacity(0);
        FluidStack fluidStack = new FluidStack(fluid, cap);
        if (handler.isFluidValid(0, fluidStack)) {
            handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
        return new FluidContainerExchange(stack, fluid, cap);
    }

    public static void fillWithAllFluids(Consumer<ItemStack> items, ItemLike item) {
        for (Fluid fluid : Library.wrapIterator(BuiltInRegistries.FLUID.iterator())) {
            if (!fluid.isSource(fluid.defaultFluidState())) continue;
            items.accept(filledFluid(item, fluid));
        }
    }
    public static ItemStack filledFluid(ItemLike item, Fluid fluid) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) throw new IllegalStateException("Cannot fill item without fluid handler!");
        int cap = handler.getTankCapacity(0);
        FluidStack fluidStack = new FluidStack(fluid, cap);
        if (handler.isFluidValid(0, fluidStack)) {
            handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
        return stack;
    }
}
