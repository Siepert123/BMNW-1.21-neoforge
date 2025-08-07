package nl.melonstudios.bmnw.weapon.missile;

import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import nl.melonstudios.bmnw.init.BMNWTags;

public enum MissileFuelType {
    SOLID_FUEL(),
    KEROSENE(FluidIngredient.tag(BMNWTags.Fluids.LIQUID_KEROSENE), FluidIngredient.tag(BMNWTags.Fluids.LIQUID_OXYGEN));

    private final boolean isSolidFuel;
    private final FluidIngredient reactant;
    private final FluidIngredient oxidizer;

    MissileFuelType() {
        this.isSolidFuel = true;
        this.reactant = null;
        this.oxidizer = null;
    }
    MissileFuelType(FluidIngredient reactant, FluidIngredient oxidizer) {
        this.isSolidFuel = false;
        this.reactant = reactant;
        this.oxidizer = oxidizer;
    }

    public boolean isSolidFuel() {
        return this.isSolidFuel;
    }
    public FluidIngredient getReactant() {
        return this.reactant;
    }
    public FluidIngredient getOxidizer() {
        return this.oxidizer;
    }
}
