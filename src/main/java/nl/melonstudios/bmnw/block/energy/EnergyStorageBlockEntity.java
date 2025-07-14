package nl.melonstudios.bmnw.block.energy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.container.fluid.FluidFlow;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IAsBlock;
import nl.melonstudios.bmnw.interfaces.IBatteryItem;
import nl.melonstudios.bmnw.interfaces.IFlowCfg;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;
import nl.melonstudios.bmnw.screen.EnergyStorageMenu;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnergyStorageBlockEntity extends SyncedBlockEntity implements ITickable, IAsBlock<EnergyStorageBlock>, MenuProvider, IFlowCfg {
    public static final int SLOT_BATTERY_IN = 0;
    public static final int SLOT_BATTERY_OUT = 1;

    public EnergyStorageBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.ENERGY_STORAGE.get(), pos, blockState);

        this.energyStorage = new ExtendedEnergyStorage(this.getAsBlock().capacity) {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                int i = super.receiveEnergy(toReceive, simulate);
                if (!simulate && i != 0) EnergyStorageBlockEntity.this.setChanged();
                return i;
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                int i = super.extractEnergy(toExtract, simulate);
                if (!simulate && i != 0) EnergyStorageBlockEntity.this.setChanged();
                return i;
            }
        };
    }

    private final ExtendedEnergyStorage energyStorage;
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            EnergyStorageBlockEntity.this.notifyChange();
        }
    };
    private final IEnergyStorage energyInterface = new IEnergyStorage() {
        private ExtendedEnergyStorage getEnergy() {
            return EnergyStorageBlockEntity.this.energyStorage;
        }

        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            return this.canReceive() ? this.getEnergy().receiveEnergy(toReceive, simulate) : 0;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return this.canExtract() ? this.getEnergy().extractEnergy(toExtract, simulate) : 0;
        }

        @Override
        public int getEnergyStored() {
            return this.getEnergy().getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return this.getEnergy().getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return EnergyStorageBlockEntity.this.getFlow().out;
        }

        @Override
        public boolean canReceive() {
            return EnergyStorageBlockEntity.this.getFlow().in;
        }
    };

    @Nullable
    public IEnergyStorage getEnergyInterface(@Nullable Direction context) {
        if (context == null) return this.energyStorage;
        return this.energyInterface;
    }

    public FluidFlow flowRedstoneOff = FluidFlow.IN_ONLY;
    public FluidFlow flowRedstoneOn = FluidFlow.OUT_ONLY;
    public FluidFlow getFlow() {
        return this.getBlockState().getValue(BlockStateProperties.POWERED) ? this.flowRedstoneOn : this.flowRedstoneOff;
    }

    public final DataSlot energyTracker = new DataSlot() {
        @Override
        public int get() {
            return EnergyStorageBlockEntity.this.energyStorage.getEnergyStored();
        }

        @Override
        public void set(int value) {
            EnergyStorageBlockEntity.this.energyStorage.setEnergyStored(value);
        }
    };

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.putInt("energy", this.energyStorage.getEnergyStored());
        nbt.put("Inventory", this.inventory.serializeNBT(registries));

        nbt.putByte("flowRedstoneOff", (byte)this.flowRedstoneOff.ordinal());
        nbt.putByte("flowRedstoneOn", (byte)this.flowRedstoneOn.ordinal());
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.energyStorage.setEnergyStored(nbt.getInt("energy"));
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));

        this.flowRedstoneOff = FluidFlow.VALUES[nbt.getByte("flowRedstoneOff")];
        this.flowRedstoneOn = FluidFlow.VALUES[nbt.getByte("flowRedstoneOn")];
    }

    @Override
    public EnergyStorageBlock getAsBlock() {
        return (EnergyStorageBlock) this.getBlockState().getBlock();
    }

    @Override
    public Component getDisplayName() {
        return this.getAsBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnergyStorageMenu(containerId, playerInventory, this, this.energyTracker);
    }

    @Override
    public void update() {
        ItemStack batteryIn = this.inventory.getStackInSlot(SLOT_BATTERY_IN);
        if (this.energyStorage.getEnergyStored() < this.energyStorage.getMaxEnergyStored()
                && batteryIn.getItem() instanceof IBatteryItem battery) {
            int transfer = battery.getMaxEnergyTransfer();
            int cap = Math.min(this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored(), transfer);
            int max = battery.tryRemoveEnergy(batteryIn, cap);
            battery.removeStoredEnergy(batteryIn, this.energyStorage.receiveEnergy(max, false));
            this.notifyChange();
        }

        ItemStack batteryOut = this.inventory.getStackInSlot(SLOT_BATTERY_OUT);
        if (this.energyStorage.getEnergyStored() > 0
                && batteryOut.getItem() instanceof IBatteryItem battery) {
            int transfer = battery.getMaxEnergyTransfer();
            int cap = Math.min(this.energyStorage.getEnergyStored(), transfer);
            int max = battery.tryInsertEnergy(batteryOut, cap);
            battery.addStoredEnergy(batteryOut, this.energyStorage.extractEnergy(max, false));
            this.notifyChange();
        }
    }

    @Override
    public void cycle(boolean redstone) {
        if (redstone) {
            this.flowRedstoneOn = this.flowRedstoneOn.next();
        } else {
            this.flowRedstoneOff = this.flowRedstoneOff.next();
        }
        this.notifyChange();
        this.level.invalidateCapabilities(this.worldPosition);
    }
}
