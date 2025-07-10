package nl.melonstudios.bmnw.logistics;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlock;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlockEntity;
import nl.melonstudios.bmnw.block.state.PipeConnectionProperty;
import nl.melonstudios.bmnw.misc.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;

public final class PipeNet {
    private static final Logger LOGGER = LogManager.getLogger("PipeNet");

    private final long networkID;
    private ServerLevel level;

    public void setLevel(ServerLevel level) {
        if (this.level == null) {
            this.fluidHandler = new FluidHandlerPipeNet(level, this);
            this.level = level;
        }
    }

    final LongOpenHashSet pipePositions = new LongOpenHashSet();
    final ArrayList<PipeFluidHandlerGetter> fluidHandlerGetters = new ArrayList<>();
    private FluidHandlerPipeNet fluidHandler;

    public int getNetSize() {
        return this.pipePositions.size();
    }
    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    public PipeNet(long networkID) {
        this.networkID = networkID;
        LOGGER.debug("Created new network with ID {}", networkID);
    }

    public PipeNet(CompoundTag nbt) {
        this.networkID = nbt.getLong("networkID");

        for (long l : nbt.getLongArray("PipePositions")) {
            this.pipePositions.add(l);
        }

        ListTag nbtFluidHandlerGetters = nbt.getList("FluidHandlerGetters", Tag.TAG_COMPOUND);
        for (int i = 0; i < nbtFluidHandlerGetters.size(); i++) {
            CompoundTag compoundTag = nbtFluidHandlerGetters.getCompound(i);
            this.addFluidHandlerGetter(PipeFluidHandlerGetter.deserialize(compoundTag));
        }
    }

    public CompoundTag writeNBT(CompoundTag nbt) {
        LOGGER.debug("Saving network with ID {} ({} pipe positions, {} fluid handler getters)",
                this.networkID, this.pipePositions.size(), this.fluidHandlerGetters.size()
        );

        nbt.putLong("networkID", this.networkID);

        nbt.putLongArray("PipePositions", this.pipePositions.toArray(new long[0]));

        ListTag nbtFluidHandlerGetters = new ListTag();
        for (PipeFluidHandlerGetter fluidHandlerGetter : this.fluidHandlerGetters) {
            nbtFluidHandlerGetters.add(fluidHandlerGetter.serialize());
        }
        nbt.put("FluidHandlerGetters", nbtFluidHandlerGetters);

        return nbt;
    }

    public void merge(Level level, PipeNet other) {
        if (other == this) return;

        LOGGER.debug("Merging network {} and {}", this.networkID, other.networkID);

        this.pipePositions.addAll(other.pipePositions);
        this.fluidHandlerGetters.addAll(other.fluidHandlerGetters);

        this.convertAllPipes(level, other);
    }

    private void convertAllPipes(Level level, PipeNet net) {
        LOGGER.debug("Converting {} pipes", net.pipePositions.size());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (long l : net.pipePositions) {
            pos.set(l);
            if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be) {
                be.setNetworkID(this.networkID);
            }
        }
        net.pipePositions.clear();
    }

    @Override
    public String toString() {
        return "PipeNet[ID:" + Long.toHexString(this.networkID) + "]";
    }

    public long getNetworkID() {
        return this.networkID;
    }

    public void addFluidPipeAndAllTheShenanigans(FluidPipeBlockEntity be) {
        LOGGER.debug("Added pipe at {} to network {}", be.getBlockPos(), this.networkID);
        BlockPos pos = be.getBlockPos();
        if (this.pipePositions.add(pos.asLong())) {
            Level level = Objects.requireNonNull(be.getLevel(), "block entity level is null");
            be.setNetworkID(this.networkID);
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
                PipeConnectionProperty property = FluidPipeBlock.getSideProperty(face.getOpposite());
                if (be.getAsBlock().getPipeStateConnection(level, pos,
                        be.getBlockState().getValue(property).isForcedOff(), face.getOpposite()).isConnected()) {
                    mutable.setWithOffset(pos, face.getOpposite());
                    IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, face);
                    if (handler == null) continue;
                    this.addFluidHandlerGetter(new PipeFluidHandlerGetter(mutable.immutable(), face, handler.getTanks()));
                }
            }
        }
        this.getLevelPipeNets().setDirty();
    }

    private final ArrayList<PipeFluidHandlerGetter> delegationListeners = new ArrayList<>();
    public void addFluidHandlerGetter(PipeFluidHandlerGetter fhg) {
        LOGGER.debug("Adding FluidHandlerGetter {}", fhg);
        this.fluidHandlerGetters.add(fhg); //I assume the fluid handler getter is valid...!
        if (this.level != null) {
            this.level.registerCapabilityListener(fhg.pos(), () -> {
                LOGGER.debug("Capability {} was invalidated :(", fhg);
                this.fluidHandlerGetters.remove(fhg);
                return false;
            });
            for (PipeFluidHandlerGetter delegation : this.delegationListeners) {
                this.level.registerCapabilityListener(delegation.pos(), () -> {
                    LOGGER.debug("Capability {} was invalidated... :(", fhg);
                    this.fluidHandlerGetters.remove(fhg);
                    return false;
                });
            }
            this.delegationListeners.clear();
            this.getLevelPipeNets().setDirty();
        } else {
            this.delegationListeners.add(fhg);
        }
    }

    public void pipeRemoved(BlockPos pos) {
        long asLong = pos.asLong();
        if (this.pipePositions.remove(asLong)) { //Only break up and reconnect network if it actually changed
            LOGGER.debug("Pipe at {} has left network {} :(", pos, this.networkID);
            this.removeDisconnectedHandlerGetters();

            LongSet positions = new LongArraySet(this.pipePositions);
            this.pipePositions.clear();

            LevelPipeNets levelPipeNets = this.getLevelPipeNets();
            levelPipeNets.removeIfEmpty(this.networkID);
            levelPipeNets.setDirty();

            this.rejoinNetworks(levelPipeNets, positions);
        }
    }

    private void rejoinNetworks(LevelPipeNets levelPipeNets, LongSet positions) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (long packedPos : positions) {
            pos.set(packedPos);
            BlockState state = this.level.getBlockState(pos);
            if (state.getBlock() instanceof FluidPipeBlock block) {
                block.onPlace(state, this.level, pos, Blocks.AIR.defaultBlockState(), false);
            }
        }
        levelPipeNets.setDirty();
    }

    private void removeDisconnectedHandlerGetters() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        this.fluidHandlerGetters.removeIf((fhg) -> {
            mutable.setWithOffset(fhg.pos(), fhg.face());
            return !PipeNet.this.pipePositions.contains(mutable.asLong());
        });
        this.getLevelPipeNets().setDirty();
    }
    public void validateFluidHandlerGetters(Level level) {
        this.fluidHandlerGetters.removeIf((fluidHandlerGetter) -> !fluidHandlerGetter.stillValid(level));
    }

    public void removeFluidHandlerGettersAt(long pos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        LOGGER.debug("Removing FluidHandlerGetters at {}", mutable.set(pos));
        this.fluidHandlerGetters.removeIf((fhg) -> {
            mutable.setWithOffset(fhg.pos(), fhg.face());
            return mutable.asLong() == pos;
        });
        this.getLevelPipeNets().setDirty();
    }

    public void forceFHGUpdate(BlockPos pos) {
        FluidPipeBlock.evaluateFluidHandlers(this.level, pos);
    }

    public LevelPipeNets getLevelPipeNets() {
        return LevelPipeNets.get(this.level);
    }
}
