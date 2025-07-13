package nl.melonstudios.bmnw.block.logistics;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.capabilities.Capabilities;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.IAsBlock;
import nl.melonstudios.bmnw.logistics.cables.ICableNetPropagator;
import nl.melonstudios.bmnw.misc.Library;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CableBlockEntity extends BlockEntity implements ICableNetPropagator, IAsBlock<CableBlock> {
    public CableBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.CABLE.get(), pos, blockState);
    }

    @Override
    public CableBlock getAsBlock() {
        return (CableBlock) this.getBlockState().getBlock();
    }

    @Nullable
    private Long network;

    @Override
    public void setNetworkID(long id) {
        this.network = id;
    }
    @Override
    public void removeNetworkID() {
        this.network = null;
    }

    @Override
    @Nullable
    public Long getNetworkID() {
        return this.network;
    }

    @Override
    public boolean connectsToFace(Direction face) {
        return true;
    }

    @Override
    public boolean connectsUp() {
        return true;
    }
    @Override
    public boolean connectsDown() {
        return true;
    }
    @Override
    public boolean connectsNorth() {
        return true;
    }
    @Override
    public boolean connectsEast() {
        return true;
    }
    @Override
    public boolean connectsSouth() {
        return true;
    }
    @Override
    public boolean connectsWest() {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.network != null) tag.putLong("networkID", this.network);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("networkID", Tag.TAG_LONG)) this.network = tag.getLong("networkID");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity & ICableNetPropagator> void ensureCorrectState() {
        assert this.level != null;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockState state = this.getBlockState();
        for (Direction face : Library.DIRECTIONS_WITHOUT_NULL) {
            BooleanProperty property = CableBlock.getFaceProperty(face);
            boolean flag = false;
            mutable.setWithOffset(this.worldPosition, face);
            BlockEntity temp = this.level.getBlockEntity(mutable);
            if (temp instanceof ICableNetPropagator) {
                T be = (T) temp;
                if (be.connectsToFace(face.getOpposite())) flag = true;
            } else if (this.level.getCapability(Capabilities.EnergyStorage.BLOCK, mutable, face.getOpposite()) != null) {
                flag = true;
            }
            state = state.setValue(property, flag);
        }
        this.level.setBlockAndUpdate(this.worldPosition, state);
    }
}
