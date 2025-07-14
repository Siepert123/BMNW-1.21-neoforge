package nl.melonstudios.bmnw.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.container.fluid.FluidBarrelBlock;
import nl.melonstudios.bmnw.block.container.fluid.FluidFlow;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.interfaces.IFlowCfg;
import nl.melonstudios.bmnw.interfaces.IInfiniteFluidSupply;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.item.tools.FluidContainerItem;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.screen.FluidBarrelMenu;
import org.jetbrains.annotations.Nullable;

public class FluidBarrelBlockEntity extends SyncedBlockEntity implements ITickable, MenuProvider, IFlowCfg {
    public static final int SLOT_IN_FULL = 0;
    public static final int SLOT_IN_EMPTY = 1;
    public static final int SLOT_OUT_EMPTY = 2;
    public static final int SLOT_OUT_FULL = 3;
    public static final int SLOT_FLUID_IDENTIFIER = 4;
    public FluidBarrelBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.FLUID_BARREL.get(), pos, blockState);
        this.inventory = new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                FluidBarrelBlockEntity.this.notifyChange();
            }

            @Override
            public int getSlotLimit(int slot) {
                return slot == SLOT_IN_FULL || slot == SLOT_OUT_EMPTY || slot == SLOT_FLUID_IDENTIFIER ? 1 : Item.ABSOLUTE_MAX_STACK_SIZE;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot == SLOT_FLUID_IDENTIFIER ? stack.is(BMNWItems.FLUID_IDENTIFIER.get()) : true;
            }
        };
        this.tank = new FluidTank(this.getBlock().getCapacity(), (stack) -> stack.is(this.fluidType)) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                FluidBarrelBlockEntity.this.notifyChange();
            }
        };
    }

    private FluidBarrelBlock getBlock() {
        return (FluidBarrelBlock) this.getBlockState().getBlock();
    }

    public final ItemStackHandler inventory;
    public final FluidTank tank;
    private final IFluidHandler fluidInterface = new IFluidHandler() {
        private FluidTank getFluid() {
            return FluidBarrelBlockEntity.this.tank;
        }
        private FluidFlow getFlow() {
            return FluidBarrelBlockEntity.this.getFluidFlow();
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return this.getFluid().getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.getFluid().getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.is(FluidBarrelBlockEntity.this.fluidType);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return this.getFlow().in ? this.isFluidValid(0, resource) ? this.getFluid().fill(resource, action) : 0 : 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return this.getFlow().out ? this.getFluid().drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return this.getFlow().out ? this.getFluid().drain(maxDrain, action) : FluidStack.EMPTY;
        }
    };
    @Nullable
    public IFluidHandler getFluidInterface(@Nullable Direction face) {
        return face == null || face.getAxis() == Direction.Axis.Y ? this.fluidInterface : null;
    }
    public Fluid fluidType = Fluids.EMPTY;
    public FluidFlow flowRedstoneOff = FluidFlow.IN_ONLY;
    public FluidFlow flowRedstoneOn = FluidFlow.OUT_ONLY;
    public FluidFlow getFluidFlow() {
        return this.getBlockState().getValue(BlockStateProperties.POWERED) ? this.flowRedstoneOn : this.flowRedstoneOff;
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
        nbt.put("Tank", this.tank.writeToNBT(registries, new CompoundTag()));
        nbt.putString("fluidType", BuiltInRegistries.FLUID.getKey(this.fluidType).toString());
        nbt.putByte("flowRedstoneOff", (byte)this.flowRedstoneOff.ordinal());
        nbt.putByte("flowRedstoneOn", (byte)this.flowRedstoneOn.ordinal());
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
        this.tank.readFromNBT(registries, nbt.getCompound("Tank"));
        this.fluidType = BuiltInRegistries.FLUID.get(ResourceLocation.parse(nbt.getString("fluidType")));
        this.flowRedstoneOff = FluidFlow.VALUES[nbt.getByte("flowRedstoneOff")];
        this.flowRedstoneOn = FluidFlow.VALUES[nbt.getByte("flowRedstoneOn")];
    }

    public void drops() {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Library.dropItem(this.level, this.worldPosition, stack.copy());
        }
    }

    @Override
    public void update() {
        if (!this.level.isClientSide) {
            ItemStack fluidIdentifier = this.inventory.getStackInSlot(4);
            if (!fluidIdentifier.isEmpty() && fluidIdentifier.has(BMNWDataComponents.FLUID_TYPE)) {
                Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidIdentifier.get(BMNWDataComponents.FLUID_TYPE)));
                if (fluid != this.fluidType) {
                    this.fluidType = fluid;
                    this.tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                    this.notifyChange();
                }
            }
            if (!this.fluidType.isSame(Fluids.EMPTY)) {
                if (this.tank.getSpace() > 0) {
                    this.processInputs();
                }
                if (this.tank.getFluidAmount() > 0) {
                    this.processOutputs();
                }

                if (this.getFluidFlow().out && this.tank.getFluidAmount() > 0) {
                    this.tryPushFluids();
                }
            }
        }
    }

    private void processInputs() {
        ItemStack in = this.inventory.getStackInSlot(SLOT_IN_FULL);
        if (in.getItem() instanceof IInfiniteFluidSupply supply && supply.compatible(in, this.fluidType)) {
            this.tank.fill(new FluidStack(this.fluidType, supply.transferSpeed(in)), IFluidHandler.FluidAction.EXECUTE);
            this.notifyChange();
        } else {
            IFluidHandlerItem handler = FluidContainerItem.getHandler(in);
            fluid:
            if (handler != null) {
                FluidStack drained = handler.drain(new FluidStack(this.fluidType, this.tank.getSpace()),
                        IFluidHandler.FluidAction.SIMULATE);
                if (drained.isEmpty()) {
                    ItemStack remain = this.inventory.insertItem(SLOT_IN_EMPTY, handler.getContainer(), false);
                    this.inventory.setStackInSlot(SLOT_IN_FULL, remain);
                    this.notifyChange();
                    break fluid;
                }
                this.tank.fill(handler.drain(drained, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                if (handler.getFluidInTank(0).isEmpty()) {
                    ItemStack remain = this.inventory.insertItem(SLOT_IN_EMPTY, handler.getContainer(), false);
                    this.inventory.setStackInSlot(SLOT_IN_FULL, remain);
                }
                this.notifyChange();
            }
        }
    }
    private void processOutputs() {
        ItemStack out = this.inventory.getStackInSlot(SLOT_OUT_EMPTY);
        if (out.getItem() instanceof IInfiniteFluidSupply supply && supply.compatible(out, this.fluidType)) {
            this.tank.drain(supply.transferSpeed(out), IFluidHandler.FluidAction.EXECUTE);
            this.notifyChange();
        } else {
            IFluidHandlerItem handler = FluidContainerItem.getHandler(out);
            fluid:
            if (handler != null) {
                int filled = handler.fill(this.tank.getFluid().copy(), IFluidHandler.FluidAction.SIMULATE);
                if (filled == 0) break fluid;
                handler.fill(new FluidStack(this.fluidType, filled), IFluidHandler.FluidAction.EXECUTE);
                this.tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                if (handler.getTankCapacity(0) <= handler.getFluidInTank(0).getAmount()) {
                    ItemStack remain = this.inventory.insertItem(SLOT_OUT_FULL, handler.getContainer().copy(), false);
                    this.inventory.setStackInSlot(SLOT_OUT_EMPTY, remain);
                }
                this.notifyChange();
            }
        }
    }
    private void tryPushFluids() {
        {
            IFluidHandler above = Capabilities.FluidHandler.BLOCK.getCapability(this.level, this.worldPosition.above(),
                    this.level.getBlockState(this.worldPosition.above()), this.level.getBlockEntity(this.worldPosition.above()), Direction.DOWN);
            if (above != null) {
                int cap = above.fill(this.tank.getFluid().copy(), IFluidHandler.FluidAction.SIMULATE);
                this.tank.drain(above.fill(this.tank.getFluid().copyWithAmount(cap), IFluidHandler.FluidAction.EXECUTE),
                        IFluidHandler.FluidAction.EXECUTE);
            }

            if (this.tank.isEmpty()) return;
        }
        {
            IFluidHandler below = Capabilities.FluidHandler.BLOCK.getCapability(this.level, this.worldPosition.below(),
                    this.level.getBlockState(this.worldPosition.below()), this.level.getBlockEntity(this.worldPosition.below()), Direction.UP);
            if (below != null) {
                int cap = below.fill(this.tank.getFluid().copy(), IFluidHandler.FluidAction.SIMULATE);
                this.tank.drain(below.fill(this.tank.getFluid().copyWithAmount(cap), IFluidHandler.FluidAction.EXECUTE),
                        IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FluidBarrelMenu(containerId, playerInventory, this);
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
