package nl.melonstudios.bmnw.logistics.cables;

import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import nl.melonstudios.bmnw.logistics.pipes.PipeNet;
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
public final class CableNetManager extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger("CableNetManager");
    private static final Factory<CableNetManager> FACTORY = new Factory<>(
            CableNetManager::new, CableNetManager::read
    );
    private static final String FILE_NAME = "BMNW_cable_networks";
    public static void createIfNecessary(ServerLevel level) {
        CableNetManager manager = level.getDataStorage().computeIfAbsent(FACTORY, FILE_NAME);
        manager.level = level;
        for (CableNet cableNet : manager.cableNetworks.values()) {
            cableNet.setLevel(level);
        }
        MANAGER_CACHE.put(level, manager);
    }
    public static CableNetManager get(ServerLevel level) {
        return Objects.requireNonNull(MANAGER_CACHE.get(level), "unavailable :(");
    }

    public static CableNetManager read(CompoundTag tag, HolderLookup.Provider registries) {
        CableNetManager manager = new CableNetManager();
        manager.nextNetworkID = tag.getLong("nextNetworkID");
        ListTag list = tag.getList("CableNets", Tag.TAG_COMPOUND);
        if (list.isEmpty()) return manager;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag net = list.getCompound(i);
            try {
                CableNet cableNet = new CableNet(net);
                manager.cableNetworks.put(cableNet.networkID, cableNet);
            } catch (CableNetException e) {
                LOGGER.error("Could not load CableNet\n" + net, e);
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
        if (!this.cableNetworks.isEmpty()) {
            ListTag list = new ListTag();
            for (CableNet cableNet : this.cableNetworks.values()) {
                list.add(cableNet.serialize(new CompoundTag()));
            }
            nbt.put("CableNets", list);
        }
        return nbt;
    }

    private static final IdentityHashMap<ServerLevel, CableNetManager> MANAGER_CACHE = new IdentityHashMap<>();
    public static void clear(ServerStoppedEvent event) {
        if (!event.getServer().isStopped()) {
            LOGGER.error("but...");
        }
        MANAGER_CACHE.clear();
    }

    private ServerLevel level;
    private CableNetManager() {}

    private final Long2ObjectArrayMap<CableNet> cableNetworks = new Long2ObjectArrayMap<>();
    private long nextNetworkID = 0L;

    private long createNewUniqueNetworkID() {
        this.setDirty();
        return ++this.nextNetworkID;
    }
    private CableNet getOrCreateNetwork(long id) {
        this.setDirty();
        return this.cableNetworks.computeIfAbsent(id, (k) -> {
            CableNet cableNet = new CableNet(k);
            cableNet.level = this.level;
            return cableNet;
        });
    }

    public <T extends BlockEntity & ICableNetPropagator> void handlePlacedCable(ServerLevel level, T be, boolean updateNetwork) {
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        be.ensureCorrectState();
        BlockPos pos = be.getBlockPos();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Set<CableNet> mergeQueue = new ObjectArraySet<>();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            if (be.connectsToFace(face)) {
                mutablePos.setWithOffset(pos, face);
                if (level.getBlockEntity(mutablePos) instanceof ICableNetPropagator neighbour) {
                    if (neighbour.getNetworkID() == null) continue;
                    if (neighbour.connectsToFace(face.getOpposite())) {
                        long neighbourID = neighbour.getNonNullNetworkID();
                        if (be.getNetworkID() == null) {
                            be.setNetworkID(neighbourID);
                        } else if (neighbour.getNonNullNetworkID() != neighbourID) {
                            CableNet cableNet = this.cableNetworks.get(neighbourID);
                            if (cableNet == null || cableNet.cablePositions.isEmpty()) continue;
                            mergeQueue.add(cableNet);
                        }
                    }
                }
            }
        }
        if (be.getNetworkID() == null) {
            be.setNetworkID(this.createNewUniqueNetworkID());
        }
        long id = be.getNonNullNetworkID();
        CableNet net = this.getOrCreateNetwork(id);
        if (net.addNewCable(be) & updateNetwork) {
            net.forceUpdate(level);
        }
        for (CableNet merge : mergeQueue) {
            net.merge(level, merge, false);
        }
        if (!mergeQueue.isEmpty() & updateNetwork) {
            net.forceUpdate(level);
        }
        this.setDirty();
    }

    public <T extends BlockEntity & ICableNetPropagator> void handleUpdatedCable(ServerLevel level, T be) {
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        be.ensureCorrectState();
        BlockPos pos = be.getBlockPos();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Set<CableNet> mergeQueue = new ObjectArraySet<>();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            if (be.connectsToFace(face)) {
                mutablePos.setWithOffset(pos, face);
                if (level.getBlockEntity(mutablePos) instanceof ICableNetPropagator neighbour) {
                    if (neighbour.getNetworkID() == null) continue;
                    if (neighbour.connectsToFace(face.getOpposite())) {
                        long neighbourID = neighbour.getNonNullNetworkID();
                        if (be.getNetworkID() == null) {
                            be.setNetworkID(neighbourID);
                        } else if (be.getNonNullNetworkID() != neighbourID) {
                            CableNet cableNet = this.cableNetworks.get(neighbourID);
                            if (cableNet == null || cableNet.cablePositions.isEmpty()) continue;
                            mergeQueue.add(cableNet);
                        }
                    }
                }
            }
        }
        long id = be.getNonNullNetworkID();
        CableNet cableNet = this.getOrCreateNetwork(id);

        for (CableNet merge : mergeQueue) {
            cableNet.merge(level, merge, false);
        }

        cableNet.forceUpdate(level);
        this.setDirty();
    }

    public <T extends BlockEntity & ICableNetPropagator> void handleRemovedCable(ServerLevel level, T be) {
        if (be.getNetworkID() == null) {
            LOGGER.warn("Ignored removed cable with no network ID");
            return;
        }
        if (be.getLevel() != level) throw new IllegalArgumentException("BlockEntity level does not match target level");
        long id = be.getNonNullNetworkID();
        CableNet net = this.cableNetworks.get(id);
        if (net == null) {
            LOGGER.warn("Ignored removed cable with invalid network ID");
            return;
        }

        net.cablePositions.remove(be.getBlockPos());
        Set<BlockPos> positions = new HashSet<>(net.cablePositions);
        for (BlockPos pos : positions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof ICableNetPropagator) {
                T member = (T) temp;
                member.removeNetworkID();
            }
        }
        net.cablePositions.clear();
        net.energyStorageLocations.clear();

        this.reconnectCables(level, positions);

        this.cleanup();
        this.setDirty();
    }

    public <T extends BlockEntity & ICableNetPropagator> void reconnectCables(ServerLevel level, Set<BlockPos> positions) {
        Set<CableNet> updatableCableNets = new ObjectArraySet<>();
        for (BlockPos pos : positions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof ICableNetPropagator) {
                T be = (T) temp;
                this.handlePlacedCable(level, be, false);
                long networkID = be.getNonNullNetworkID();
                updatableCableNets.add(this.cableNetworks.get(networkID));
            }
        }
        updatableCableNets.forEach(cableNet -> cableNet.forceUpdate(this.level));

        this.cleanup();
        this.setDirty();
    }

    public void cleanup() {
        LongList bin = new LongArrayList();
        for (CableNet net : this.cableNetworks.values()) {
            if (net.cablePositions.isEmpty()) bin.add(net.networkID);
        }
        for (long delete : bin) {
            this.cableNetworks.remove(delete);
        }
        if (!bin.isEmpty()) {
            LOGGER.debug("Removed {} empty cable networks", bin.size());
            this.setDirty();
        }
    }

    @Nullable
    public CableNetEnergyStorage getEnergyStorage(ICableNetPropagator be) {
        Long network = be.getNetworkID();
        if (network == null) return null;
        CableNet net = this.cableNetworks.get(network.longValue());
        return net != null ? net.energyStorage : null;
    }

    @Nonnull
    public ServerLevel getLevel() {
        return Objects.requireNonNull(this.level, "level not yet set.");
    }

    public void dumpDebug(CommandContext<CommandSourceStack> context) {
        StringBuilder builder = new StringBuilder("PipeNets for level " + this.getLevel().dimension().location() + ":");
        for (CableNet net : this.cableNetworks.values()) {
            builder.append("\n").append("  PipeNet#").append(net.networkID).append( " (").append(net.cablePositions.size()).append(" members)");
        }
        context.getSource().sendSuccess(
                () -> Component.literal(
                        builder.toString()
                ),
                false
        );
    }
}
