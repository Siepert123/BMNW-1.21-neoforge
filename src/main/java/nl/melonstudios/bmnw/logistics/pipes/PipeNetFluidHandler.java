package nl.melonstudios.bmnw.logistics.pipes;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeNetFluidHandler implements IFluidHandler {
    private static final ThreadLocal<List<IFluidHandler>> MUTABLE_HANDLER_LIST = ThreadLocal.withInitial(ObjectArrayList::new);
    private static final ThreadLocal<List<FluidHandlerLocation>> MUTABLE_LOCATION_LIST = ThreadLocal.withInitial(ObjectArrayList::new);
    private static final ThreadLocal<Random> MUTABLE_RANDOM = ThreadLocal.withInitial(Random::new);

    private final PipeNet pipeNet;

    public PipeNetFluidHandler(PipeNet pipeNet) {
        this.pipeNet = pipeNet;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0; //Prevent unnecessary calculations if the fluid is empty or air
        if (resource.is(NeoForgeMod.EMPTY_TYPE.value())) return 0;

        ServerLevel level = this.pipeNet.level;
        List<IFluidHandler> candidates = MUTABLE_HANDLER_LIST.get();
        candidates.clear();
        List<FluidHandlerLocation> sortedLocations = MUTABLE_LOCATION_LIST.get();
        sortedLocations.clear();
        sortedLocations.addAll(this.pipeNet.fluidHandlerLocations);
        Random random = MUTABLE_RANDOM.get();
        long seed = System.nanoTime();
        sortedLocations.sort((l, r) -> {
            if (l.pos() == r.pos()) return 0;
            int height = Integer.compare(l.pos().getY(), r.pos().getY());
            if (height != 0) return height;
            random.setSeed(seed ^ (l.pos().hashCode() | Integer.toUnsignedLong(r.pos().hashCode()) << 32));
            return random.nextBoolean() ? -1 : 1;
        });
        for (FluidHandlerLocation location : sortedLocations) {
            IFluidHandler handler = location.getFluidHandlerAt(level);
            if (handler != null && handler.fill(resource, FluidAction.SIMULATE) > 0) candidates.add(handler);
        }
        {
            Set<IFluidHandler> set = new LinkedHashSet<>(candidates);
            candidates.clear();
            candidates.addAll(set);
            set.clear();
        }

        int remaining = resource.getAmount();
        int filled = 0;


        for (IFluidHandler handler : candidates) {
            if (remaining <= 0) break;
            int amount = handler.fill(resource.copyWithAmount(remaining), action);
            remaining -= amount;
            filled += amount;
        }

        return filled;
    }

    @Override //We do not support draining for now
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override //We do not support draining for now
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public String toString() {
        return "PipeNetFluidHandler[ID:" + Long.toHexString(this.pipeNet.networkID) + "]";
    }
}
