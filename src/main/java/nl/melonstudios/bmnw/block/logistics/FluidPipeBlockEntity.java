package nl.melonstudios.bmnw.block.logistics;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IAsBlock;
import nl.melonstudios.bmnw.logistics.LevelPipeNets;
import nl.melonstudios.bmnw.logistics.PipeNet;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidPipeBlockEntity extends BlockEntity implements IAsBlock<FluidPipeBlock> {
    public FluidPipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.FLUID_PIPE.get(), pos, blockState);
    }

    @Override
    public FluidPipeBlock getAsBlock() {
        return (FluidPipeBlock) this.getBlockState().getBlock();
    }

    private Long networkID;
    public void setNetworkID(long networkID) {
        this.networkID = networkID;
    }
    @UnknownNullability
    public Long getNetworkID() {
        return this.networkID;
    }
    public long getNonnullNetworkID() {
        return Objects.requireNonNull(this.getNetworkID(), "wow");
    }

    @Nullable
    public PipeNet getPipeNet() {
        if (this.networkID == null) return null;
        if (!this.level.isClientSide && this.level instanceof ServerLevel level) {
            return LevelPipeNets.get(level).getNetByID(this.networkID);
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.networkID != null) tag.putLong("networkID", this.networkID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.networkID = tag.contains("networkID") ? tag.getLong("networkID") : null;
    }
}