package nl.melonstudios.bmnw.block.entity.custom;

import nl.melonstudios.bmnw.interfaces.IMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class AlloyBlastFurnaceBlockEntity extends BlockEntity implements IEnergyStorage, IMachineBlockEntity {
    public AlloyBlastFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public int work() {
        return 0;
    }

    @Override
    public int maxWork() {
        return 0;
    }

    @Override
    public int idle() {
        return 0;
    }

    @Override
    public int idleTime() {
        return 0;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
