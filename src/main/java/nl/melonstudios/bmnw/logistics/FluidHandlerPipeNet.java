package nl.melonstudios.bmnw.logistics;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidHandlerPipeNet implements IFluidHandler {
    private static final ThreadLocal<List<PipeFluidHandlerGetter>> THREAD_SAFE_LIST = ThreadLocal.withInitial(ArrayList::new);

    private final ServerLevel level;
    private final PipeNet pipeNet;

    public FluidHandlerPipeNet(ServerLevel level, PipeNet pipeNet) {
        this.level = level;
        this.pipeNet = pipeNet;
    }

    @Deprecated
    public void recountTanks() {}

    public List<PipeFluidHandlerGetter> getFHGs() {
        return this.pipeNet.fluidHandlerGetters;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(final int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(final int tank) {
        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(final int tank, final FluidStack stack) {
        for (PipeFluidHandlerGetter fhg : this.getFHGs()) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            if (handler instanceof FluidHandlerPipeNet) return false; //Prevent a stack overflow that happens for some reason
            for (int i = 0; i < handler.getTanks(); i++) {
                if (handler.isFluidValid(i, stack)) return true;
            }
        }
        return false;
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) return 0;
        List<PipeFluidHandlerGetter> handlers = THREAD_SAFE_LIST.get();
        handlers.clear();
        loop:
        for (PipeFluidHandlerGetter fhg : this.getFHGs()) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            for (int i = 0; i < handler.getTanks(); i++) {
                if (handler.isFluidValid(i, resource)) {
                    handlers.add(fhg);
                    continue loop;
                }
            }
        }
        if (handlers.isEmpty()) return 0;
        handlers.sort((l, r) -> {
            int h = Integer.compare(l.pos().getY(), r.pos().getY());
            if (h != 0) return h;
            return Integer.compare(this.getTotalTankSize(l), this.getTotalTankSize(r));
        });
        final int start = resource.getAmount();
        int amountLeft = start;
        for (PipeFluidHandlerGetter fhg : handlers) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            amountLeft -= handler.fill(resource.copyWithAmount(amountLeft), action);
        }
        return start - amountLeft;
    }

    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) return resource;
        List<PipeFluidHandlerGetter> handlers = THREAD_SAFE_LIST.get();
        handlers.clear();
        for (PipeFluidHandlerGetter fhg : this.getFHGs()) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            if (handler.drain(resource, FluidAction.SIMULATE).is(resource.getFluid())) {
                handlers.add(fhg);
            }
        }
        if (handlers.isEmpty()) return FluidStack.EMPTY;
        handlers.sort((l, r) -> {
            int h = Integer.compare(r.pos().getY(), l.pos().getY());
            if (h != 0) return h;
            return Integer.compare(this.getTotalTankSize(r), this.getTotalTankSize(l));
        });
        final int start = resource.getAmount();
        int amountLeft = start;
        for (PipeFluidHandlerGetter fhg : handlers) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            FluidStack extract = handler.drain(resource.copyWithAmount(amountLeft), FluidAction.SIMULATE);
            if (extract.is(resource.getFluid())) {
                handler.drain(extract, action);
                amountLeft -= extract.getAmount();
            }
        }
        return resource.copyWithAmount(start-amountLeft);
    }

    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        List<PipeFluidHandlerGetter> handlers = THREAD_SAFE_LIST.get();
        handlers.clear();
        for (PipeFluidHandlerGetter fhg : this.getFHGs()) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            if (!handler.drain(maxDrain, FluidAction.SIMULATE).isEmpty()) {
                handlers.add(fhg);
            }
        }
        if (handlers.isEmpty()) return FluidStack.EMPTY;
        handlers.sort((l, r) -> {
            int h = Integer.compare(r.pos().getY(), l.pos().getY());
            if (h != 0) return h;
            return Integer.compare(this.getTotalTankSize(r), this.getTotalTankSize(l));
        });
        int amountLeft = maxDrain;
        Fluid type = null;
        for (PipeFluidHandlerGetter fhg : handlers) {
            IFluidHandler handler = fhg.getFluidHandler(this.level);
            FluidStack extract = handler.drain(amountLeft, FluidAction.SIMULATE);
            if (type == null) {
                type = extract.getFluid();
            }
            if (extract.is(type)) {
                handler.drain(amountLeft, action);
                amountLeft -= extract.getAmount();
            }
        }
        if (type == null) return FluidStack.EMPTY;
        return new FluidStack(type, maxDrain -amountLeft);
    }

    private int getTotalTankSize(PipeFluidHandlerGetter fhg) {
        return getTotalTankSize(fhg.getFluidHandler(this.level));
    }
    private static int getTotalTankSize(IFluidHandler handler) {
        int size = 0;
        for (int i = 0; i < handler.getTanks(); i++) {
            size += handler.getTankCapacity(i);
        }
        return size;
    }
}
