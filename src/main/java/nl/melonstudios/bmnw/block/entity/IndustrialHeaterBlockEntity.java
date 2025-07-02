package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IHeatable;
import nl.melonstudios.bmnw.interfaces.ISlaveOwner;
import nl.melonstudios.bmnw.interfaces.ITickable;

public class IndustrialHeaterBlockEntity extends SyncedBlockEntity implements ITickable, ISlaveOwner<IndustrialHeaterBlockEntity> {
    public IndustrialHeaterBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.INDUSTRIAL_HEATER.get(), pos, blockState);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            IndustrialHeaterBlockEntity.this.notifyChange();
        }
    };

    public int storedHeat = 0;


    public boolean initialized = false;
    @Override
    public IndustrialHeaterBlockEntity getMaster() {
        return this;
    }
    @Override
    public void checkSlaves() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        boolean valid = true;
        loop:
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mutable.setWithOffset(this.worldPosition, x, 0, z);
                if (this.worldPosition.asLong() == mutable.asLong()) continue;
                BlockEntity be = this.level.getBlockEntity(mutable);
                if (!(be instanceof DummyBlockEntity dummy && dummy.getCore() == this.worldPosition)) {
                    valid = false;
                    break loop;
                }
            }
        }
        if (!valid) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(this.worldPosition, x, 0, z);
                    if (this.worldPosition.asLong() == mutable.asLong()) continue;
                    BlockEntity be = this.level.getBlockEntity(mutable);
                    if (be instanceof DummyBlockEntity dummy && dummy.getCore() == this.worldPosition) {
                        dummy.setIntentionalDeletion();
                        this.level.destroyBlock(mutable, false);
                    }
                }
            }
            this.level.destroyBlock(this.worldPosition, true);
        }
    }
    @Override
    public boolean hasInitialized() {
        return this.initialized;
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));

        if (!packet) {
            nbt.putBoolean("initialized", this.initialized);
            nbt.putInt("storedHeat", this.storedHeat);
            nbt.putInt("burnTime", this.burnTime);
            nbt.putInt("totalBurnTime", this.totalBurnTime);
        }
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));

        if (!packet) {
            this.initialized = nbt.getBoolean("initialized");
            this.storedHeat = nbt.getInt("storedHeat");
            this.burnTime = nbt.getInt("burnTime");
            this.totalBurnTime = nbt.getInt("totalBurnTime");
        }
    }

    public int burnTime, totalBurnTime;
    @Override
    public void update() {
        this.getRidOfHeat();
        this.updateBurnTimer();
        if (this.totalBurnTime == 0) this.burnNewItem();
        this.updateState();
        this.setChanged();
    }

    private void getRidOfHeat() {
        if (this.storedHeat > 0) {
            BlockPos above = this.worldPosition.above();
            BlockState state = this.level.getBlockState(above);
            if (state.getBlock() instanceof IHeatable heatable) {
                if (heatable.canAcceptHeat(this.level, above, state)) {
                    this.storedHeat -= heatable.acceptHeat(this.level, above, state, this.storedHeat);
                    this.notifyChange();
                }
            }
        }
    }
    private void updateBurnTimer() {
        if (this.burnTime-- <= 0) {
            this.totalBurnTime = 0;
        }
    }
    private void burnNewItem() {
        if (this.level.isClientSide) return;
        ItemStack firstStack = this.inventory.getStackInSlot(1);
        ItemStack secondStack = this.inventory.getStackInSlot(0);

        ItemStack selectedStack = null;

        if (!firstStack.isEmpty() && firstStack.getBurnTime(null) > 0) selectedStack = firstStack;
        else if (!secondStack.isEmpty() && secondStack.getBurnTime(null) > 0) selectedStack = secondStack;

        if (selectedStack != null) {
            int burnTime = Mth.ceil(selectedStack.getBurnTime(null) * this.getBurnTimeMultiplier(selectedStack));
            this.burnTime = this.totalBurnTime = burnTime;
            selectedStack.shrink(1);
            this.notifyChange();
        }
    }
    private void updateState() {
        if (this.level.isClientSide) return;
        boolean isBurning = this.totalBurnTime > 0;
        if (this.getBlockState().getValue(BlockStateProperties.LIT) != isBurning) {
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.LIT, isBurning), 3);
        }
    }

    private float getBurnTimeMultiplier(ItemStack stack) {
        return 1.0F;
    }
}
