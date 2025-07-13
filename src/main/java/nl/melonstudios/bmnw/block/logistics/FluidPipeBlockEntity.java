package nl.melonstudios.bmnw.block.logistics;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import nl.melonstudios.bmnw.block.state.PipeConnectionProperty;
import nl.melonstudios.bmnw.enums.PipeConnection;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IAsBlock;
import nl.melonstudios.bmnw.logistics.pipes.IPipeNetPropagator;
import nl.melonstudios.bmnw.misc.Library;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidPipeBlockEntity extends BlockEntity implements IPipeNetPropagator, IAsBlock<FluidPipeBlock> {
    public FluidPipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.FLUID_PIPE.get(), pos, blockState);
    }

    @Override
    public FluidPipeBlock getAsBlock() {
        return (FluidPipeBlock) this.getBlockState().getBlock();
    }

    private Long networkID;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.networkID != null) tag.putLong("networkID", this.networkID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.networkID = tag.contains("networkID", Tag.TAG_LONG) ? tag.getLong("networkID") : null;
    }

    @Override
    public void setNetworkID(long id) {
        this.networkID = id;
    }
    @Override
    public void removeNetworkID() {
        this.networkID = null;
    }
    @Override
    public Long getNetworkID() {
        return this.networkID;
    }

    @Override
    public boolean connectsUp() {
        return !this.getBlockState().getValue(FluidPipeBlock.UP).isForcedOff();
    }
    @Override
    public boolean connectsDown() {
        return !this.getBlockState().getValue(FluidPipeBlock.DOWN).isForcedOff();
    }
    @Override
    public boolean connectsNorth() {
        return !this.getBlockState().getValue(FluidPipeBlock.NORTH).isForcedOff();
    }
    @Override
    public boolean connectsEast() {
        return !this.getBlockState().getValue(FluidPipeBlock.EAST).isForcedOff();
    }
    @Override
    public boolean connectsSouth() {
        return !this.getBlockState().getValue(FluidPipeBlock.SOUTH).isForcedOff();
    }
    @Override
    public boolean connectsWest() {
        return !this.getBlockState().getValue(FluidPipeBlock.WEST).isForcedOff();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity & IPipeNetPropagator> void ensureCorrectState() {
        assert this.level != null;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockState state = this.getBlockState();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            PipeConnectionProperty property = FluidPipeBlock.getSideProperty(face);
            if (state.getValue(property).isForcedOff()) continue;
            boolean flag = false;
            mutable.setWithOffset(this.worldPosition, face);
            BlockEntity temp = this.level.getBlockEntity(mutable);
            if (temp instanceof IPipeNetPropagator) {
                T be = (T) temp;
                if (be.connectsToFace(face.getOpposite())) flag = true;
            } else {
                if (this.level.getCapability(Capabilities.FluidHandler.BLOCK, mutable, face.getOpposite()) != null) {
                    flag = true;
                }
            }
            state = state.setValue(property, PipeConnection.fromBoolean(flag));
        }
        this.level.setBlockAndUpdate(this.worldPosition, state);
    }
}