package nl.melonstudios.bmnw.logistics.pipes;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import nl.melonstudios.bmnw.misc.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unchecked")
public final class PipeNetManager extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger("PipeNetManager");
    private static final Factory<PipeNetManager> FACTORY = new Factory<>(
            PipeNetManager::new, PipeNetManager::read
    );
    private static final String FILE_NAME = "BMNW_pipe_networks";
    public static void createIfNecessary(ServerLevel level) {
        PipeNetManager manager = level.getDataStorage().computeIfAbsent(FACTORY, FILE_NAME);
        manager.level = level;
        for (PipeNet pipeNet : manager.pipeNetworks.values()) {
            pipeNet.setLevel(level);
        }
        MANAGER_CACHE.put(level, manager);
    }
    public static PipeNetManager get(ServerLevel level) {
        return Objects.requireNonNull(MANAGER_CACHE.get(level), "unavailable :(");
    }

    public static PipeNetManager read(CompoundTag tag, HolderLookup.Provider registries) {
        PipeNetManager manager = new PipeNetManager();
        manager.nextNetworkID = tag.getLong("nextNetworkID");
        ListTag list = tag.getList("PipeNets", Tag.TAG_COMPOUND);
        if (list.isEmpty()) return manager;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag net = list.getCompound(i);
            try {
                PipeNet pipeNet = new PipeNet(net);
                manager.pipeNetworks.put(pipeNet.networkID, pipeNet);
            } catch (PipeNetException e) {
                LOGGER.error("Could not load PipeNet\n" + net, e);
                manager.setDirty();
            }
        }
        return manager;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return this.write(tag);
    }

    private CompoundTag write(CompoundTag nbt) {
        nbt.putLong("nextNetworkID", this.nextNetworkID);
        if (!this.pipeNetworks.isEmpty()) {
            ListTag list = new ListTag();
            for (PipeNet pipeNet : this.pipeNetworks.values()) {
                list.add(pipeNet.serialize(new CompoundTag()));
            }
            nbt.put("PipeNets", list);
        }
        return nbt;
    }

    private static final IdentityHashMap<ServerLevel, PipeNetManager> MANAGER_CACHE = new IdentityHashMap<>();
    public static void clear(ServerStoppedEvent event) {
        if (!event.getServer().isStopped()) {
            System.out.println("?");
        }
        MANAGER_CACHE.clear();
    }

    private ServerLevel level;
    private PipeNetManager() {}

    private final Long2ObjectArrayMap<PipeNet> pipeNetworks = new Long2ObjectArrayMap<>();
    private long nextNetworkID = 0L;

    private long createNewUniqueNetworkID() {
        this.setDirty();
        return ++nextNetworkID;
    }
    private PipeNet getOrCreateNetwork(long id) {
        this.setDirty();
        return this.pipeNetworks.computeIfAbsent(id, (k) -> {
            PipeNet pipeNet = new PipeNet(k);
            pipeNet.level = this.level;
            return pipeNet;
        });
    }

    /**
     * Handles a new pipe block being placed.
     * ("Pipe block" is a term used for any block that propagates the pipe network)
     * @param level The server level
     * @param be The pipe block entity to add
     * @param updateNetwork Whether to update the network (false if mass placing pipes)
     */
    public <T extends BlockEntity & IPipeNetPropagator> void handlePlacedPipe(ServerLevel level, T be, boolean updateNetwork) {
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        be.ensureCorrectState();
        BlockPos pos = be.getBlockPos();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Set<PipeNet> mergeQueue = new ObjectArraySet<>();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            if (be.connectsToFace(face)) {
                mutablePos.setWithOffset(pos, face);
                if (level.getBlockEntity(mutablePos) instanceof IPipeNetPropagator neighbour) {
                    if (neighbour.getNetworkID() == null) continue;
                    if (neighbour.connectsToFace(face.getOpposite())) {
                        long neighbourID = neighbour.getNonNullNetworkID();
                        if (be.getNetworkID() == null) {
                            be.setNetworkID(neighbourID);
                        } else if (be.getNonNullNetworkID() != neighbourID) {
                            PipeNet pipeNet = this.pipeNetworks.get(neighbourID);
                            if (pipeNet == null || pipeNet.pipePositions.isEmpty()) continue;
                            mergeQueue.add(pipeNet);
                        }
                    }
                }
            }
        }
        if (be.getNetworkID() == null) {
            be.setNetworkID(this.createNewUniqueNetworkID());
        }
        long id = be.getNonNullNetworkID();
        PipeNet net = this.getOrCreateNetwork(id);
        if (net.addNewPipe(be) & updateNetwork) {
            net.forceUpdate(level);
        }
        for (PipeNet merge : mergeQueue) {
            net.merge(level, merge, false);
        }
        if (!mergeQueue.isEmpty() & updateNetwork) {
            net.forceUpdate(level);
        }
        this.setDirty();
    }

    public <T extends BlockEntity & IPipeNetPropagator> void handleUpdatedPipe(ServerLevel level, T be) {
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        be.ensureCorrectState();
        BlockPos pos = be.getBlockPos();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Set<PipeNet> mergeQueue = new ObjectArraySet<>();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            if (be.connectsToFace(face)) {
                mutablePos.setWithOffset(pos, face);
                if (level.getBlockEntity(mutablePos) instanceof IPipeNetPropagator neighbour) {
                    if (neighbour.getNetworkID() == null) continue;
                    if (neighbour.connectsToFace(face.getOpposite())) {
                        long neighbourID = neighbour.getNonNullNetworkID();
                        if (be.getNetworkID() == null) {
                            be.setNetworkID(neighbourID);
                        } else if (be.getNonNullNetworkID() != neighbourID) {
                            PipeNet pipeNet = this.pipeNetworks.get(neighbourID);
                            if (pipeNet == null || pipeNet.pipePositions.isEmpty()) continue;
                            mergeQueue.add(pipeNet);
                        }
                    }
                }
            }
        }
        long id = be.getNonNullNetworkID();
        PipeNet pipeNet = this.getOrCreateNetwork(id);

        for (PipeNet merge : mergeQueue) {
            pipeNet.merge(level, merge, false);
        }

        pipeNet.forceUpdate(level);
        this.setDirty();
    }

    public <T extends BlockEntity & IPipeNetPropagator> void handleRemovedPipe(ServerLevel level, T be) {
        if (be.getNetworkID() == null) {
            LOGGER.warn("Ignored removed pipe with no network ID");
            return;
        }
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        long id = be.getNonNullNetworkID();
        PipeNet net = this.pipeNetworks.get(id);
        if (net == null) {
            LOGGER.warn("Ignored removed pipe with invalid network ID");
            return;
        }

        net.pipePositions.remove(be.getBlockPos());
        Set<BlockPos> positions = new HashSet<>(net.pipePositions);
        for (BlockPos pos : positions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof IPipeNetPropagator) {
                T member = (T) temp;
                member.removeNetworkID();
            }
        }
        net.pipePositions.clear();
        net.fluidHandlerLocations.clear();

        this.reconnectPipes(level, positions);

        this.cleanup();
        this.setDirty();
    }

    public <T extends BlockEntity & IPipeNetPropagator> void reconnectPipes(ServerLevel level, Set<BlockPos> positions) {
        Set<PipeNet> updatablePipeNets = new ObjectArraySet<>();
        for (BlockPos pos : positions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof IPipeNetPropagator) {
                T be = (T) temp;
                this.handlePlacedPipe(level, be, false);
                long networkID = be.getNonNullNetworkID();
                updatablePipeNets.add(this.pipeNetworks.get(networkID));
            }
        }
        updatablePipeNets.forEach(pipeNet -> pipeNet.forceUpdate(this.level));

        this.cleanup();
        this.setDirty();
    }

    public void cleanup() {
        LongList bin = new LongArrayList();
        for (PipeNet net : this.pipeNetworks.values()) {
            if (net.pipePositions.isEmpty()) bin.add(net.networkID);
        }
        for (long delete : bin) {
            this.pipeNetworks.remove(delete);
        }
        if (!bin.isEmpty()) {
            LOGGER.debug("Removed {} empty pipe nets", bin.size());
            this.setDirty();
        }
    }

    @Nullable
    public PipeNetFluidHandler getFluidHandler(IPipeNetPropagator be) {
        Long network = be.getNetworkID();
        if (network == null) return null;
        PipeNet net = this.pipeNetworks.get(network.longValue());
        return net != null ? net.fluidHandler : null;
    }

    @Nonnull
    public ServerLevel getLevel() {
        return Objects.requireNonNull(this.level, "level not yet set.");
    }
}
