package com.siepert.bmnw.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class AbstractFluidBarrelBlockEntity extends BlockEntity implements IFluidTank {
    public AbstractFluidBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int capacity, List<Fluid> fluidList, boolean blacklist) {
        super(type, pos, blockState);
        this.capacity = capacity;
        this.fluidList = fluidList;
        this.blacklist = blacklist;
    }

    protected final int capacity;
    protected FluidStack storedFluid = FluidStack.EMPTY;


    protected final List<Fluid> fluidList;
    private final boolean blacklist;

    @Override
    public FluidStack getFluid() {
        return storedFluid;
    }

    @Override
    public int getFluidAmount() {
        return storedFluid.getAmount();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        if (blacklist) {
            return !fluidList.contains(stack.getFluid());
        }
        return fluidList.contains(stack.getFluid());
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.is(storedFluid.getFluid())) {
            int space = capacity - storedFluid.getAmount();
            if (space >= resource.getAmount()) {
                if (action.execute()) {
                    storedFluid.grow(resource.getAmount());
                    resource.setAmount(0);
                }
                return resource.getAmount();
            } else {
                if (action.execute()) {
                    storedFluid.setAmount(capacity);
                    resource.shrink(space);
                }
                return space;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (storedFluid.getAmount() >= maxDrain) {
            Fluid fluid = storedFluid.getFluid();
            if (action.execute()) {
                storedFluid.shrink(maxDrain);
                if (storedFluid.isEmpty()) storedFluid = FluidStack.EMPTY;
            }
            return new FluidStack(fluid, maxDrain);
        }
        int diff = storedFluid.getAmount();
        Fluid fluid = storedFluid.getFluid();
        if (action.execute()) storedFluid = FluidStack.EMPTY;
        return new FluidStack(fluid, diff);
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.is(storedFluid.getFluid())) {
            if (storedFluid.getAmount() >= resource.getAmount()) {
                if (action.execute()) {
                    storedFluid.shrink(resource.getAmount());
                    if (storedFluid.isEmpty()) storedFluid = FluidStack.EMPTY;
                }
                return resource.copy();
            }
            int diff =  storedFluid.getAmount();
            if (action.execute()) storedFluid = FluidStack.EMPTY;
            return new FluidStack(resource.getFluid(), diff);
        }
        return FluidStack.EMPTY;
    }
}
