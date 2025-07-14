package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ISlaveOwner;

import javax.annotation.Nullable;

public final class DummyBlockEntity extends SyncedBlockEntity implements MenuProvider {
    public DummyBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.DUMMY.get(), pos, blockState);
    }

    private BlockPos core;
    public void setCore(BlockPos pos) {
        this.core = pos;
        this.notifyChange();
    }
    public BlockPos getCore() {
        return this.core;
    }

    @Nullable
    public BlockEntity getCoreBE() {
        return this.level.getBlockEntity(this.core);
    }
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getCoreBE(Class<T> cast) {
        BlockEntity be = this.getCoreBE();
        if (cast.isInstance(be)) return (T) be;
        return null;
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        if (this.core != null) nbt.putLong("core", this.core.asLong());
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.core = nbt.contains("core") ? BlockPos.of(nbt.getLong("core")) : null;
    }

    private boolean willBeKilled = false;
    public void setIntentionalDeletion() {
        this.willBeKilled = true;
    }
    public void onRemove() {
        if (!this.willBeKilled) {
            ISlaveOwner<?> owner = this.getCoreBE(ISlaveOwner.class);
            if (owner != null && owner.hasInitialized()) owner.checkSlaves();
        }
    }

    @Override
    public Component getDisplayName() {
        MenuProvider provider = this.getCoreBE(MenuProvider.class);
        if (provider != null) return provider.getDisplayName();
        return Component.literal("null");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        MenuProvider provider = this.getCoreBE(MenuProvider.class);
        return provider == null ? null : provider.createMenu(containerId, playerInventory, player);
    }
}
