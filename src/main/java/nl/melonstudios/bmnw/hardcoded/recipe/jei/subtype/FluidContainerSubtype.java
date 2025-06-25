package nl.melonstudios.bmnw.hardcoded.recipe.jei.subtype;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import nl.melonstudios.bmnw.item.tools.FluidContainerItem;
import org.jetbrains.annotations.Nullable;

public class FluidContainerSubtype {
    private final Fluid fluid;
    public FluidContainerSubtype(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FluidContainerSubtype subtype) {
            return subtype.fluid == this.fluid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.fluid.hashCode();
    }

    @Override
    public String toString() {
        return String.format("FluidContainer[fluid=%s]", this.fluid.getFluidType().getDescription());
    }

    public static ISubtypeInterpreter<ItemStack> interpreter() {
        return Interpreter.instance;
    }

    private static class Interpreter implements ISubtypeInterpreter<ItemStack> {
        private static final Interpreter instance = new Interpreter();

        private Interpreter() {}

        @Override
        public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
            FluidStack contents = FluidContainerItem.getContents(ingredient);
            return contents.isEmpty() ? null : new FluidContainerSubtype(contents.getFluid());
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
            return "";
        }
    }
}
