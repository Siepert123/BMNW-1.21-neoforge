package nl.melonstudios.bmnw.logistics.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import nl.melonstudios.bmnw.misc.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class PipeNet {
    private static final Logger LOGGER = LogManager.getLogger("PipeNet");
    public final long networkID;

    final Set<BlockPos> pipePositions;
    final Set<FluidHandlerLocation> fluidHandlerLocations;
    final PipeNetFluidHandler fluidHandler;

    ServerLevel level;
    public void setLevel(ServerLevel level) {
        if (this.level == null) {
            this.level = level;
        }
    }

    public PipeNet(long id) {
        LOGGER.debug("Creating new PipeNet (ID:{})", () -> Long.toHexString(id));
        this.networkID = id;
        this.pipePositions = new HashSet<>();
        this.fluidHandlerLocations = new HashSet<>();

        this.fluidHandler = new PipeNetFluidHandler(this);
    }
    public PipeNet(CompoundTag nbt) throws PipeNetException {
        if (!nbt.contains("networkID", Tag.TAG_LONG)) {
            throw new PipeNetException("PipeNet NBT missing network ID");
        }
        this.networkID = nbt.getLong("networkID");

        if (nbt.contains("PipePositions", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("PipePositions", Tag.TAG_LONG);
            this.pipePositions = new HashSet<>(list.size());
            for (Tag tag : list) {
                if (tag instanceof LongTag longTag) this.pipePositions.add(BlockPos.of(longTag.getAsLong()));
            }
        } else {
            throw new PipeNetException("PipeNet has no pipe positions");
        }

        if (nbt.contains("FluidHandlerLocations", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("FluidHandlerLocations", Tag.TAG_COMPOUND);
            this.fluidHandlerLocations = new HashSet<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                FluidHandlerLocation location = FluidHandlerLocation.read(compound);
                if (location != null) this.fluidHandlerLocations.add(location);
            }
        } else {
            this.fluidHandlerLocations = new HashSet<>(0);
        }

        this.fluidHandler = new PipeNetFluidHandler(this);
    }

    @Contract("_ -> param1")
    public @NotNull CompoundTag serialize(CompoundTag nbt) {
        nbt.putLong("networkID", this.networkID);

        if (!this.pipePositions.isEmpty()) {
            ListTag list = new ListTag();
            for (BlockPos pos : this.pipePositions) {
                list.add(LongTag.valueOf(pos.asLong()));
            }
            nbt.put("PipePositions", list);
        }

        if (!this.fluidHandlerLocations.isEmpty()) {
            ListTag list = new ListTag();
            for (FluidHandlerLocation location : this.fluidHandlerLocations) {
                list.add(location.write());
            }
            nbt.put("FluidHandlerLocations", list);
        }
        return nbt;
    }

    /**
     * Adds a new pipe to the network.
     * @param be The pipe to add
     * @return True if the pipe was successfully added
     */
    public <T extends BlockEntity & IPipeNetPropagator> boolean addNewPipe(T be) {
        return this.pipePositions.add(be.getBlockPos());
    }

    /**
     * Forces an update to the network.
     */
    public <T extends BlockEntity & IPipeNetPropagator> void forceUpdate(ServerLevel level) {
        this.fluidHandlerLocations.clear();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        Set<BlockPos> ghostPositions = new HashSet<>();
        for (BlockPos pos : this.pipePositions) {
            BlockEntity temp = level.getBlockEntity(pos);
            if (temp instanceof IPipeNetPropagator) {
                T be = (T) temp;
                for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
                    mutable.setWithOffset(pos, face);
                    if (level.getBlockEntity(mutable) instanceof IPipeNetPropagator) continue;
                    if (be.connectsToFace(face)) {
                        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, mutable, face.getOpposite());
                        if (handler != null) {
                            this.fluidHandlerLocations.add(new FluidHandlerLocation(mutable.immutable(), face.getOpposite()));
                        }
                    }
                }
            } else ghostPositions.add(pos);
        }
        if (!ghostPositions.isEmpty()) {
            this.pipePositions.removeAll(ghostPositions);
        }
    }

    /**
     * Merges this network with another network.
     * @param level The level
     * @param other The other network to be merged with this one
     * @param update Whether to update the network after merging
     */
    public void merge(ServerLevel level, PipeNet other, boolean update) {
        if (other == this) return;

        this.pipePositions.addAll(other.pipePositions);
        this.fluidHandlerLocations.addAll(other.fluidHandlerLocations);

        this.convertAllPipes(level, other);

        if (update) this.forceUpdate(level);
    }

    private void convertAllPipes(ServerLevel level, PipeNet net) {
        for (BlockPos pos : net.pipePositions) {
            if (level.getBlockEntity(pos) instanceof IPipeNetPropagator be) {
                be.setNetworkID(this.networkID);
            }
        }
        net.pipePositions.clear();
    }

    @Override
    public String toString() {
        return "PipeNet[ID:" + Long.toHexString(this.networkID) + "]";
    }
}
