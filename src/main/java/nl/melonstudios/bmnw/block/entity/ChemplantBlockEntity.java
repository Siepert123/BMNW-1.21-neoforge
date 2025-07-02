package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ISlaveOwner;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;

public class ChemplantBlockEntity extends SyncedBlockEntity implements ITickable, ISlaveOwner<ChemplantBlockEntity> {
    public ChemplantBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.CHEMPLANT.get(), pos, blockState);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ChemplantBlockEntity.this.notifyChange();
        }
    };
    public final ExtendedEnergyStorage energy = new ExtendedEnergyStorage(100000) {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            int received = super.receiveEnergy(toReceive, simulate);
            if (!simulate && toReceive != 0) ChemplantBlockEntity.this.notifyChange();
            return received;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            int extracted = super.extractEnergy(toExtract, simulate);
            if (!simulate && toExtract != 0) ChemplantBlockEntity.this.notifyChange();
            return extracted;
        }
    };
    public final FluidTank fluidInput1 = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            ChemplantBlockEntity.this.notifyChange();
        }
    };
    public final FluidTank fluidInput2 = new FluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            ChemplantBlockEntity.this.notifyChange();
        }
    };

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
        nbt.putInt("energy", this.energy.getEnergyStored());
        nbt.put("FluidInput1", this.fluidInput1.writeToNBT(registries, new CompoundTag()));
        nbt.put("FluidInput2", this.fluidInput2.writeToNBT(registries, new CompoundTag()));

        nbt.putBoolean("initialized", this.initialized);
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
        this.energy.setEnergyStored(nbt.getInt("energy"));
        this.fluidInput1.readFromNBT(registries, nbt.getCompound("FluidInput1"));
        this.fluidInput2.readFromNBT(registries, nbt.getCompound("FluidInput2"));

        this.initialized = nbt.getBoolean("initialized");
        this.invalidateRenderBB();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(1);
    }

    @Override
    public void update() {

    }

    @Override
    public ChemplantBlockEntity getMaster() {
        return this;
    }

    @Override
    public void checkSlaves() {
        if (!this.initialized) return;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        boolean valid = true;
        loop:
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(this.worldPosition, x, y, z);
                    if (this.worldPosition.asLong() == mutable.asLong()) continue;
                    BlockEntity be = this.level.getBlockEntity(mutable);
                    if (!(be instanceof DummyBlockEntity dummy && dummy.getCore() == this.worldPosition)) {
                        valid = false;
                        break loop;
                    }
                }
            }
        }
        if (!valid) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutable.setWithOffset(this.worldPosition, x, y, z);
                        if (this.worldPosition.asLong() == mutable.asLong()) continue;
                        BlockEntity be = this.level.getBlockEntity(mutable);
                        if (be instanceof DummyBlockEntity dummy && dummy.getCore() == this.worldPosition) {
                            dummy.setIntentionalDeletion();
                            this.level.destroyBlock(mutable, false);
                        }
                    }
                }
            }
            this.level.destroyBlock(this.worldPosition, true);
        }
    }

    public boolean initialized = false;
    @Override
    public boolean hasInitialized() {
        return this.initialized;
    }
}
