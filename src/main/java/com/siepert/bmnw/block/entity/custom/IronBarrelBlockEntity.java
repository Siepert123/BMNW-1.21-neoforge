package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class IronBarrelBlockEntity extends BlockEntity implements IFluidTank {
    public IronBarrelBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.IRON_BARREL.get(), pos, blockState);
    }

    public IFluidHandler tank = new FluidTank(8000);
    public IFluidHandler getIFluid() {
        return tank;
    }

    @Override
    public FluidStack getFluid() {
        return tank.getFluidInTank(0);
    }

    @Override
    public int getFluidAmount() {
        return tank.getFluidInTank(0).getAmount();
    }

    @Override
    public int getCapacity() {
        return tank.getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return tank.isFluidValid(0, stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return tank.fill(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return tank.drain(maxDrain, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return tank.drain(resource, action);
    }
}
