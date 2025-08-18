package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SyncedBlockEntity extends OptimizedBlockEntity {
    public SyncedBlockEntity(BlockEntityType<? extends SyncedBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet);
    protected abstract void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet);

    @Override
    protected final void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.load(tag, registries, false);
    }

    @Override
    protected final void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.save(tag, registries, false);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.save(tag, registries, true);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        if (!tag.isEmpty()) this.load(tag, lookupProvider, true);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.load(tag, lookupProvider, true);
    }

    public void sendData() {
        if (this.level instanceof ServerLevel level)
            level.getChunkSource().blockChanged(this.worldPosition);
    }

    public void notifyChange() {
        this.setChanged();
        this.sendData();
    }
}
