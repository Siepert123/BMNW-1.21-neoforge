package nl.melonstudios.bmnw.block.energy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IAsBlock;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnergyStorageBlockEntity extends SyncedBlockEntity implements IAsBlock<EnergyStorageBlock> {
    public EnergyStorageBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.ENERGY_STORAGE.get(), pos, blockState);

        this.energyStorage = new ExtendedEnergyStorage(this.getAsBlock().capacity) {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                int i = super.receiveEnergy(toReceive, simulate);
                if (!simulate && i != 0) EnergyStorageBlockEntity.this.notifyChange();
                return i;
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                int i = super.extractEnergy(toExtract, simulate);
                if (!simulate && i != 0) EnergyStorageBlockEntity.this.notifyChange();
                return i;
            }
        };
    }

    private final ExtendedEnergyStorage energyStorage;
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            EnergyStorageBlockEntity.this.notifyChange();
        }
    };

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.putInt("energy", this.energyStorage.getEnergyStored());
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.energyStorage.setEnergyStored(nbt.getInt("energy"));
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
    }

    @Override
    public EnergyStorageBlock getAsBlock() {
        return (EnergyStorageBlock) this.getBlockState().getBlock();
    }
}
