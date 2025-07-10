package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.machines.CombustionEngineBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import nl.melonstudios.bmnw.init.BMNWTags;
import nl.melonstudios.bmnw.interfaces.IBatteryItem;
import nl.melonstudios.bmnw.interfaces.IInfiniteFluidSupply;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.item.tools.FluidContainerItem;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.screen.CombustionEngineMenu;

import javax.annotation.Nullable;

public class CombustionEngineBlockEntity extends SyncedBlockEntity implements MenuProvider, ITickable {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_WATER_BUCKET = 2;
    public static final int SLOT_EMPTY_BUCKET = 3;

    public static final int ENERGY_CAPACITY = 20000;
    public static final int WATER_CAPACITY = 8000;

    public CombustionEngineBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.COMBUSTION_ENGINE.get(), pos, blockState);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            CombustionEngineBlockEntity.this.notifyChange();
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_BATTERY || slot == SLOT_WATER_BUCKET ? 1 : Item.ABSOLUTE_MAX_STACK_SIZE;
        }
    };
    public final ExtendedEnergyStorage energy = new ExtendedEnergyStorage(ENERGY_CAPACITY) {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            int received = super.receiveEnergy(toReceive, simulate);
            if (!simulate && toReceive != 0) CombustionEngineBlockEntity.this.notifyChange();
            return received;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            int extracted = super.extractEnergy(toExtract, simulate);
            if (!simulate && toExtract != 0) CombustionEngineBlockEntity.this.notifyChange();
            return extracted;
        }
    };
    public final FluidTank fluid = new FluidTank(WATER_CAPACITY, (stack) -> stack.is(Fluids.WATER)) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CombustionEngineBlockEntity.this.notifyChange();
        }
    };

    private final IItemHandler fuelSlotInterface = new IItemHandler() {
        private ItemStackHandler getInventory() {
            return CombustionEngineBlockEntity.this.inventory;
        }
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.getInventory().getStackInSlot(SLOT_FUEL);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.getInventory().insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.getInventory().getStackInSlot(SLOT_FUEL).is(Items.BUCKET) ? this.getInventory().extractItem(SLOT_FUEL, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Items.LAVA_BUCKET) || stack.getBurnTime(null) / 8 > 0;
        }
    };
    private final IItemHandler waterBucketInterface = new IItemHandler() {
        private ItemStackHandler getInventory() {
            return CombustionEngineBlockEntity.this.inventory;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.getInventory().getStackInSlot(SLOT_WATER_BUCKET);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return this.getInventory().insertItem(SLOT_WATER_BUCKET, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Items.WATER_BUCKET) || stack.getItem() instanceof IInfiniteFluidSupply supply && supply.compatible(stack, Fluids.WATER);
        }
    };
    private final IItemHandler emptyBucketInterface = new IItemHandler() {
        private ItemStackHandler getInventory() {
            return CombustionEngineBlockEntity.this.inventory;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.getInventory().getStackInSlot(SLOT_EMPTY_BUCKET);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return this.getInventory().extractItem(SLOT_EMPTY_BUCKET, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    private final IEnergyStorage energyInterface = new IEnergyStorage() {
        private ExtendedEnergyStorage getEnergy() {
            return CombustionEngineBlockEntity.this.energy;
        }
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return this.getEnergy().extractEnergy(toExtract, simulate);
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
            return this.getEnergy().canExtract();
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    private final IFluidHandler fluidInterface = new IFluidHandler() {
        private FluidTank getFluid() {
            return CombustionEngineBlockEntity.this.fluid;
        }

        @Override
        public int getTanks() {
            return this.getFluid().getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return this.getFluid().getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.getFluid().getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return this.getFluid().isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return this.getFluid().drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return this.getFluid().drain(maxDrain, action);
        }
    };

    @Nullable
    public IItemHandler getItems(@Nullable Direction face) {
        if (face == null) return this.inventory;
        if (this.isFront(face)) return null;
        if (face == Direction.UP) return this.waterBucketInterface;
        if (face == Direction.DOWN) return this.emptyBucketInterface;
        return this.fuelSlotInterface;
    }
    @Nullable
    public IEnergyStorage getEnergy(@Nullable Direction face) {
        return this.isFront(face) ? null : this.energyInterface;
    }
    @Nullable
    public IFluidHandler getFluid(@Nullable Direction face) {
        return this.isFront(face) ? null : this.fluidInterface;
    }

    public boolean isFront(@Nullable Direction face) {
        return this.getBlockState().getValue(CombustionEngineBlock.FACING) == face;
    }

    public void drops() {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Library.dropItem(this.level, this.worldPosition, stack.copy());
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.combustion_engine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CombustionEngineMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
        nbt.putInt("energy", this.energy.getEnergyStored());
        nbt.put("Fluid", this.fluid.writeToNBT(registries, new CompoundTag()));

        nbt.putInt("burnTime", this.burnTime);
        nbt.putInt("maxBurnTime", this.maxBurnTime);
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
        this.energy.setEnergyStored(nbt.getInt("energy"));
        this.fluid.readFromNBT(registries, nbt.getCompound("Fluid"));

        this.burnTime = nbt.getInt("burnTime");
        this.maxBurnTime = nbt.getInt("maxBurnTime");
    }


    private boolean wasBoiling = false;
    public int burnTime = 0;
    public int maxBurnTime = 0;

    public final SimpleContainerData data = new SimpleContainerData(4);
    private static final ItemStack BUCKET = new ItemStack(Items.BUCKET);

    @Override
    public void update() {
        assert this.level != null;
        if (!this.level.isClientSide) {
            ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
            if (fuel.is(BMNWTags.Items.INFINITE_FUEL_SOURCES)) {
                this.burnTime = 2;
                this.maxBurnTime = 1;
            }
            boolean wasEligibleForBurning = this.maxBurnTime > 0;
            if (this.burnTime == 0) {
                this.maxBurnTime = 0;
                if (!fuel.isEmpty() && !this.fluid.isEmpty() && this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
                    if (fuel.getBurnTime(null) >> 3 > 0) {
                        this.burnTime = fuel.getBurnTime(null) >> 3;
                        this.maxBurnTime = this.burnTime;
                        if (fuel.is(Items.LAVA_BUCKET)) {
                            this.inventory.setStackInSlot(SLOT_FUEL, new ItemStack(Items.BUCKET));
                        } else {
                            this.inventory.extractItem(SLOT_FUEL, 1, false);
                        }
                        this.level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1.0F);
                        this.notifyChange();
                    }
                }
            } else {
                this.burnTime--;
                if (this.energy.receiveEnergy(25, true) > 0) {
                    if (this.fluid.drain(10, IFluidHandler.FluidAction.EXECUTE).getAmount() == 10) {
                        this.energy.receiveEnergy(25, false);
                    }
                }
                this.setChanged();
            }
            if (this.fluid.getSpace() > 0) {
                ItemStack bucket = this.inventory.getStackInSlot(SLOT_WATER_BUCKET);
                if (this.fluid.getSpace() >= 1000 && bucket.is(Items.WATER_BUCKET) && this.inventory.insertItem(SLOT_EMPTY_BUCKET, BUCKET, true).isEmpty()) {
                    this.fluid.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                    this.inventory.extractItem(SLOT_WATER_BUCKET, 1, false);
                    this.inventory.insertItem(SLOT_EMPTY_BUCKET, new ItemStack(Items.BUCKET), false);
                    this.level.playSound(null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
                    this.notifyChange();
                } else if (bucket.getItem() instanceof IInfiniteFluidSupply supply && supply.compatible(bucket, Fluids.WATER)) {
                    this.fluid.fill(new FluidStack(Fluids.WATER, supply.transferSpeed(bucket)), IFluidHandler.FluidAction.EXECUTE);
                    this.notifyChange();
                } else {
                    IFluidHandlerItem handler = FluidContainerItem.getHandler(bucket);
                    water:
                    if (handler != null) {
                        FluidStack drained = handler.drain(new FluidStack(Fluids.WATER, this.fluid.getSpace()),
                                IFluidHandler.FluidAction.SIMULATE);
                        if (drained.isEmpty()) {
                            ItemStack remain = this.inventory.insertItem(SLOT_EMPTY_BUCKET, bucket.copy(), false);
                            this.inventory.setStackInSlot(SLOT_WATER_BUCKET, remain);
                            break water;
                        }
                        this.fluid.fill(handler.drain(drained, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        if (handler.getFluidInTank(0).isEmpty()) {
                            ItemStack remain = this.inventory.insertItem(SLOT_EMPTY_BUCKET, bucket.copy(), false);
                            this.inventory.setStackInSlot(SLOT_WATER_BUCKET, remain);
                        }
                        this.notifyChange();
                    }
                }
            }
            if (this.energy.getEnergyStored() > 0) {
                ItemStack stack = this.inventory.getStackInSlot(SLOT_BATTERY);
                if (stack.getItem() instanceof IBatteryItem battery) {
                    int inserted = Math.min(battery.getMaxEnergyTransfer(), battery.tryInsertEnergy(stack, this.energy.getEnergyStored()));
                    this.energy.extractEnergy(inserted, false);
                    battery.addStoredEnergy(stack, inserted);
                    this.notifyChange();
                }
                if (this.energy.getEnergyStored() > 0) {
                    for (Direction d : Direction.values()) {
                        if (this.isFront(d)) continue;
                        IEnergyStorage external = this.level.getCapability(
                                Capabilities.EnergyStorage.BLOCK,
                                this.worldPosition.relative(d),
                                d.getOpposite()
                        );
                        if (external != null && external.canReceive()) {
                            int allowance = external.receiveEnergy(this.energy.getEnergyStored(), true);
                            int spending = this.energy.extractEnergy(allowance, true);
                            external.receiveEnergy(spending, false);
                        }
                    }
                    this.setChanged();
                }
            }

            boolean isEligibleForBurning = this.maxBurnTime > 0;
            if (wasEligibleForBurning != isEligibleForBurning) {
                this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BMNWStateProperties.ACTIVE, isEligibleForBurning), 3);
            }

            this.data.set(0, this.burnTime);
            this.data.set(1, this.maxBurnTime);
            this.data.set(2, this.energy.getEnergyStored());
            this.data.set(3, this.fluid.getFluidAmount());
        }


        if (this.level.isClientSide) {
            if (!this.wasBoiling && this.maxBurnTime > 0) {
                //DistrictHolder.clientOnly(() -> () -> CombustionEngineBlock.playSound(this));
            }
            this.wasBoiling = this.maxBurnTime > 0;
        }
    }
}
