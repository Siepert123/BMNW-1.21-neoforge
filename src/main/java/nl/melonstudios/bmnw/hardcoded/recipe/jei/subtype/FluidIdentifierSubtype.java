package nl.melonstudios.bmnw.hardcoded.recipe.jei.subtype;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidIdentifierSubtype {
    private final String fluidID;
    public FluidIdentifierSubtype(String fluidID) {
        this.fluidID = fluidID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FluidIdentifierSubtype subtype) {
            return Objects.equals(this.fluidID, subtype.fluidID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.fluidID.hashCode();
    }

    @Override
    public String toString() {
        return String.format("FluidIdentifier[fluid=%s]", this.fluidID);
    }

    public static ISubtypeInterpreter<ItemStack> interpreter() {
        return Interpreter.instance;
    }

    private static class Interpreter implements ISubtypeInterpreter<ItemStack> {
        private static final Interpreter instance = new Interpreter();
        private Interpreter() {}

        @Override
        public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
            return ingredient.has(BMNWDataComponents.FLUID_TYPE) ?
                    new FluidIdentifierSubtype(ingredient.get(BMNWDataComponents.FLUID_TYPE)) : null;
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
            return "";
        }
    }
}
