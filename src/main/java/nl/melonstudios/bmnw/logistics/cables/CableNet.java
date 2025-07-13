package nl.melonstudios.bmnw.logistics.cables;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import nl.melonstudios.bmnw.misc.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class CableNet {
    private static final Logger LOGGER = LogManager.getLogger("CableNet");
    public final long networkID;

    final Set<BlockPos> cablePositions;
    final Set<EnergyStorageLocation> energyStorageLocations;
    final CableNetEnergyStorage energyStorage;

    ServerLevel level;
    public void setLevel(ServerLevel level) {
        if (this.level == null) {
            this.level = level;
        }
    }

    public CableNet(long id) {
        LOGGER.debug("Creating new CableNet (ID:{})", Long.toHexString(id));
        this.networkID = id;
        this.cablePositions = new HashSet<>();
        this.energyStorageLocations = new HashSet<>();

        this.energyStorage = new CableNetEnergyStorage(this);
    }
    public CableNet(CompoundTag nbt) throws CableNetException {
        if (!nbt.contains("networkID", Tag.TAG_LONG)) {
            throw new CableNetException("CableNet missing network ID");
        }
        this.networkID = nbt.getLong("networkID");
        LOGGER.debug("Loading CableNet (ID:{})\n{}", Long.toHexString(this.networkID),
                new SnbtPrinterTagVisitor("  ", 0, new ArrayList<>()).visit(nbt));

        if (nbt.contains("CablePositions", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("CablePositions", Tag.TAG_LONG);
            this.cablePositions = new HashSet<>(list.size());
            for (Tag tag : list) {
                if (tag instanceof LongTag longTag) this.cablePositions.add(BlockPos.of(longTag.getAsLong()));
            }
        } else {
            throw new CableNetException("CableNet has no cable positions");
        }

        if (nbt.contains("EnergyStorageLocations", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("EnergyStorageLocations", Tag.TAG_COMPOUND);
            this.energyStorageLocations = new HashSet<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                EnergyStorageLocation location = EnergyStorageLocation.read(compound);
                if (location != null) this.energyStorageLocations.add(location);
            }
        } else {
            this.energyStorageLocations = new HashSet<>();
        }

        this.energyStorage = new CableNetEnergyStorage(this);
    }

    @Contract("_ -> param1")
    public @NotNull CompoundTag serialize(CompoundTag nbt) {
        nbt.putLong("networkID", this.networkID);

        if (!this.cablePositions.isEmpty()) {
            ListTag list = new ListTag();
            for (BlockPos pos : this.cablePositions) {
                list.add(LongTag.valueOf(pos.asLong()));
            }
            nbt.put("CablePositions", list);
        }

        if (!this.energyStorageLocations.isEmpty()) {
            ListTag list = new ListTag();
            for (EnergyStorageLocation location : this.energyStorageLocations) {
                list.add(location.write());
            }
            nbt.put("EnergyStorageLocations", list);
        }

        LOGGER.debug("Serialized CableNet (ID:{})\n{}", Long.toHexString(this.networkID),
                new SnbtPrinterTagVisitor("  ", 0, new ArrayList<>()).visit(nbt));
        return nbt;
    }

    public <T extends BlockEntity & ICableNetPropagator> boolean addNewCable(T be) {
        return this.cablePositions.add(be.getBlockPos());
    }

    public <T extends BlockEntity & ICableNetPropagator> void forceUpdate(ServerLevel level) {
        this.energyStorageLocations.clear();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Set<BlockPos> ghostPositions = new ObjectArraySet<>();
        for (BlockPos pos : this.cablePositions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof ICableNetPropagator) {
                T be = (T) temp;
                for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
                    mutable.setWithOffset(pos, face);
                    if (level.getBlockEntity(mutable) instanceof ICableNetPropagator) continue;
                    if (be.connectsToFace(face)) {
                        IEnergyStorage handler = level.getCapability(Capabilities.EnergyStorage.BLOCK, mutable, face.getOpposite());
                        if (handler != null) {
                            this.energyStorageLocations.add(new EnergyStorageLocation(mutable.immutable(), face.getOpposite()));
                        }
                    }
                }
            } else ghostPositions.add(pos);
        }

        if (!ghostPositions.isEmpty()) {
            this.cablePositions.removeAll(ghostPositions);
        }
    }

    public void merge(ServerLevel level, CableNet other, boolean update) {
        if (other == this) return;

        this.cablePositions.addAll(other.cablePositions);
        this.energyStorageLocations.addAll(other.energyStorageLocations);

        this.convertAllPipes(level, other);

        if (update) this.forceUpdate(level);
    }

    private void convertAllPipes(ServerLevel level, CableNet net) {
        for (BlockPos pos : net.cablePositions) {
            if (level.getBlockEntity(pos) instanceof ICableNetPropagator be) {
                be.setNetworkID(this.networkID);
            }
        }
        net.cablePositions.clear();
    }

    @Override
    public String toString() {
        return "CableNet[ID:" + Long.toHexString(this.networkID) + "]";
    }
}
