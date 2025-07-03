package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.interfaces.IExtendedEnergyStorage;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;
import nl.melonstudios.bmnw.misc.Library;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class ElectricWireConnectorBlockEntity extends WireAttachedBlockEntity implements ITickable {
    public ElectricWireConnectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.ELECTRIC_WIRE_CONNECTOR.get(), pos, blockState);
    }

    private final IExtendedEnergyStorage cachedEnergy = new ExtendedEnergyStorage(1024);

    public Direction getFacing() {
        return this.getBlockState().getValue(BlockStateProperties.FACING);
    }
    public BlockPos getAttachedPos() {
        return this.worldPosition.relative(this.getFacing().getOpposite());
    }

    private static final Vec3 WIRE_COLOR = new Vec3(160/255.0, 53/255.0, 37/255.0);
    @Override
    public Vec3 getWireColor() {
        return WIRE_COLOR;
    }

    @Nullable
    private IEnergyStorage getAttachedEnergyStorage() {
        return Objects.requireNonNull(this.level).getCapability(Capabilities.EnergyStorage.BLOCK, this.getAttachedPos(), this.getFacing());
    }

    @Nullable
    public IExtendedEnergyStorage getEnergy(@Nullable Direction side) {
        return this.getFacing().getOpposite() == side || side == null ? this.cachedEnergy : null;
    }

    public boolean removeAllConnections() {
        if (this.level instanceof ServerLevel level) {
            this.loadAllConnectedChunks();
            if (this.connections.isEmpty()) {
                return false;
            }
            HashSet<BlockPos> connectionsCopy = new HashSet<>(this.connections);
            for (BlockPos pos : connectionsCopy) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ElectricWireConnectorBlockEntity connector) {
                    connector.removeConnection(this.worldPosition, false);
                }
                Library.dropItem(this.level, this.worldPosition, BMNWItems.WIRE_SPOOL.toStack());
            }
            this.connections.clear();
            this.notifyChange();
        }
        return true;
    }

    public void onRemove() {
        this.removeAllConnections();
    }

    private ArrayList<ElectricWireConnectorBlockEntity> getConnectedBlockEntities() {
        assert this.level != null;
        ArrayList<ElectricWireConnectorBlockEntity> list = new ArrayList<>();
        for (BlockPos pos : this.connections) {
            BlockEntity be = this.level.getBlockEntity(pos);
            if (be instanceof ElectricWireConnectorBlockEntity connector) list.add(connector);
        }
        return list;
    }

    public void loadAllConnectedChunks() {
        if (this.level == null) return;
        for (BlockPos pos : this.connections) {
            this.level.getChunk(pos);
        }
    }
    public void removeConnection(BlockPos pos, boolean dropWire) {
        if (!this.connections.contains(pos)) return;
        this.connections.remove(pos);
        if (dropWire) {
            Library.dropItem(this.level, this.worldPosition, BMNWItems.WIRE_SPOOL.toStack());
        }
        this.notifyChange();
    }
    public boolean addConnection(BlockPos pos) {
        if (this.connections.contains(pos)) return false;
        if (this.worldPosition.distSqr(pos) > BMNWServerConfig.maxWireLengthSqr()) return false;
        BlockEntity be = Objects.requireNonNull(this.level).getBlockEntity(pos);
        if (be instanceof ElectricWireConnectorBlockEntity connector) {
            this.connections.add(pos);
            connector.addConnection(this.worldPosition);
            this.notifyChange();
            return true;
        }
        return false;
    }

    private void loadConnections(long[] data) {
        this.invalidateWireCache();
        this.connections.clear();
        for (long l : data) {
            this.connections.add(BlockPos.of(l));
        }
    }
    private long[] saveConnections() {
        int next = 0;
        long[] data = new long[this.connections.size()];
        for (BlockPos pos : this.connections) {
            data[next++] = pos.asLong();
        }
        return data;
    }

    private void invalidateWireCache() {
        this.cachedRenderConnections = null;
        this.invalidateRenderBB();
    }

    private final HashSet<BlockPos> connections = new HashSet<>();
    private HashSet<Vec3> cachedRenderConnections = new HashSet<>();
    public void precacheWireConnections() {
        this.cachedRenderConnections = new HashSet<>();
        for (BlockPos pos : this.connections) {
            this.cachedRenderConnections.add(pos.getCenter());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Collection<Vec3> wireConnectionsForRendering() {
        if (this.cachedRenderConnections == null) this.precacheWireConnections();
        return this.cachedRenderConnections;
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.loadConnections(nbt.getLongArray("connections"));
        this.cachedEnergy.setEnergyStored(nbt.getInt("cachedEnergy"));
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.putLongArray("connections", this.saveConnections());
        nbt.putInt("cachedEnergy", this.cachedEnergy.getEnergyStored());
    }

    @Override
    public void update() {
        assert this.level != null;
        this.level.getProfiler().push("insert energy");
        if (this.cachedEnergy.getEnergyStored() > 0) {
            IEnergyStorage attachedStorage = this.getAttachedEnergyStorage();
            if (attachedStorage != null && attachedStorage.canReceive()) {
                int inserted = attachedStorage.receiveEnergy(this.cachedEnergy.getEnergyStored(), false);
                this.cachedEnergy.setEnergyStored(this.cachedEnergy.getEnergyStored()-inserted);
            }
        }
        this.level.getProfiler().popPush("propagate energy");
        spread:
        if (this.cachedEnergy.getEnergyStored() > 0 && !this.connections.isEmpty()) {
            ArrayList<ElectricWireConnectorBlockEntity> connected = this.getConnectedBlockEntities();
            connected.removeIf(Objects::isNull);
            if (connected.isEmpty()) break spread;
            int spread = this.cachedEnergy.getEnergyStored() / connected.size();
            int remaining = this.cachedEnergy.getEnergyStored() - (spread * connected.size());
            for (ElectricWireConnectorBlockEntity be : connected) {
                remaining += be.addEnergy(spread);
            }
            this.cachedEnergy.setEnergyStored(remaining);
        }
        this.level.getProfiler().pop();
    }

    private int addEnergy(int amount) {
        return this.cachedEnergy.receiveEnergy(amount, false);
    }
}
