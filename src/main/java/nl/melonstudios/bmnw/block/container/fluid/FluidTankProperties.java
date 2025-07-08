package nl.melonstudios.bmnw.block.container.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import nl.melonstudios.bmnw.init.BMNWTags;

import java.util.function.Predicate;

public class FluidTankProperties {
    public interface ICopyable {
        default int getCapacity() {
            return 0;
        }
        default boolean allowCorrosive() {
            return false;
        }
    }

    private FluidTankProperties() {}

    public static FluidTankProperties create() {
        return new FluidTankProperties();
    }
    public static FluidTankProperties copy(ICopyable copyable) {
        return create()
                .setCapacity(copyable.getCapacity())
                .setAllowCorrosive(copyable.allowCorrosive());
    }

    public int capacity = 1000;
    public FluidTankProperties setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public boolean allowCorrosive = false;
    public FluidTankProperties setAllowCorrosive(boolean allowCorrosive) {
        this.allowCorrosive = allowCorrosive;
        return this;
    }
}
