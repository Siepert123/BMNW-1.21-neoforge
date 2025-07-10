package nl.melonstudios.bmnw.logistics;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicLong;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class LevelPipeNets extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger("LevelPipeNets");

    private static final Factory<LevelPipeNets> FACTORY = new Factory<>(
            LevelPipeNets::new, LevelPipeNets::load
    );
    private static final String FILE_NAME = "BMNW_pipe_networks";
    public static LevelPipeNets get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, FILE_NAME).setLevel(level);
    }
    private static LevelPipeNets load(CompoundTag nbt, HolderLookup.Provider registries) {
        return new LevelPipeNets().load(nbt);
    }
    private LevelPipeNets setLevel(ServerLevel level) {
        if (this.level == null) {
            this.level = level;
            for (PipeNet pipeNet : this.long2PipeNetMap.values()) {
                pipeNet.setLevel(level);
            }
        }
        return this;
    }
    private LevelPipeNets() {}

    private long idGetter = 0L;
    private ServerLevel level;
    private final Long2ObjectMap<PipeNet> long2PipeNetMap = new Long2ObjectArrayMap<>();

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag nbtPipeNets = new ListTag();
        for (PipeNet pipeNet : this.long2PipeNetMap.values()) {
            if (pipeNet.getNetSize() == 0) {
                LOGGER.warn("Skipped saving empty network {}", pipeNet.getNetworkID());
                continue;
            }
            nbtPipeNets.add(pipeNet.writeNBT(new CompoundTag()));
        }
        nbt.put("PipeNets", nbtPipeNets);

        nbt.putLong("idGetter", this.idGetter);

        return nbt;
    }

    private LevelPipeNets load(CompoundTag nbt) {
        ListTag nbtPipeNets = nbt.getList("PipeNets", Tag.TAG_COMPOUND);
        for (int i = 0; i < nbtPipeNets.size(); i++) {
            CompoundTag pipeNetNBT = nbtPipeNets.getCompound(i);
            PipeNet pipeNet = new PipeNet(pipeNetNBT);
            if (pipeNet.getNetSize() == 0) {
                LOGGER.warn("Skipped loading empty network {}", pipeNet.getNetworkID());
                continue;
            }
            this.long2PipeNetMap.put(pipeNet.getNetworkID(), pipeNet);
        }

        this.idGetter = nbt.getLong("idGetter");

        return this;
    }

    @Nullable
    public PipeNet getNetByID(long id) {
        return this.long2PipeNetMap.get(id);
    }

    public void removeIfEmpty(long id) {
        PipeNet net = this.getNetByID(id);
        if (net == null) return;
        if (net.getNetSize() == 0) {
            this.long2PipeNetMap.remove(id);
            this.setDirty();
            LOGGER.debug("Removed empty pipe net with ID {}", Long.toHexString(id));
        }
    }

    public void cleanup() {
        LongList bin = new LongArrayList();
        for (PipeNet net : this.long2PipeNetMap.values()) {
            if (net.getNetSize() == 0) bin.add(net.getNetworkID());
        }
        if (!bin.isEmpty()) this.setDirty();
        for (long id : bin) this.long2PipeNetMap.remove(id);
    }

    public int getNetworkCount() {
        return this.long2PipeNetMap.size();
    }

    private long createNetworkID(FluidPipeBlockEntity be) {
        this.setDirty();
        return this.idGetter++;
    }
    public void createNewNetworkFor(FluidPipeBlockEntity be) {
        long id = this.createNetworkID(be);
        PipeNet pipeNet = new PipeNet(id);
        pipeNet.setLevel((ServerLevel) be.getLevel());
        this.long2PipeNetMap.put(id, pipeNet);
        pipeNet.addFluidPipeAndAllTheShenanigans(be);
    }

    @Nullable
    public PipeNet getPipeNetAt(BlockPos pos) {
        long asLong = pos.asLong();
        for (PipeNet net : this.long2PipeNetMap.values()) {
            if (net.pipePositions.contains(asLong)) return net;
        }
        return null;
    }
    @Nullable
    public PipeNet getPipeNetAt(long pos) {
        for (PipeNet net : this.long2PipeNetMap.values()) {
            if (net.pipePositions.contains(pos)) return net;
        }
        return null;
    }
}
