package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.interfaces.IExtendedEnergyStorage;
import nl.melonstudios.bmnw.interfaces.IRadioAntennaStructureBlock;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;
import org.jetbrains.annotations.UnknownNullability;

public class RadioAntennaControllerBlockEntity extends SyncedBlockEntity implements ITickable {
    public RadioAntennaControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.RADIO_ANTENNA_CONTROLLER.get(), pos, blockState);
    }

    private final StructureData structureData = new StructureData();
    public final IExtendedEnergyStorage energy = new ExtendedEnergyStorage(4096);
    @Override
    public void update() {
        if (this.level.getGameTime() % 10 == 0) this.recalculateStructure();
        if (this.structureData.isValid() && this.energy.getEnergyStored() > 0) {
            this.energy.extractEnergy(this.structureData.getPowerConsumption(), false);
        }
    }

    public void recalculateStructure() {
        if (this.level instanceof ServerLevel level) {
            Direction enforcedFacing = null;
            this.structureData.resetData();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            if (!level.getBlockState(this.worldPosition.above()).is(BMNWBlocks.STEEL_DECO_BLOCK.get())) {
                return;
            }
            for (int i = 0; i < 64; i++) {
                pos.setWithOffset(this.worldPosition, 0, i+2, 0);
                BlockState state = level.getBlockState(pos);
                if (state.is(BMNWBlocks.ANTENNA_TOP.get())) {
                    this.structureData.height = i;
                    this.structureData.valid = true;
                    break;
                }
                if (state.is(BMNWBlocks.ANTENNA_DISH.get())) {
                    switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                        case NORTH -> this.structureData.nw++;
                        case WEST -> this.structureData.sw++;
                        case SOUTH -> this.structureData.se++;
                        case EAST -> this.structureData.ne++;
                    }
                    continue;
                }
                if (state.getBlock() instanceof IRadioAntennaStructureBlock structureBlock) {
                    if (enforcedFacing == null) enforcedFacing = structureBlock.getFacing(state);
                    if (!structureBlock.allowsArbitraryFacing() && structureBlock.getFacing(state) != enforcedFacing) {
                        this.structureData.height = i;
                        this.structureData.valid = false;
                        break;
                    }
                    continue;
                }
                this.structureData.height = i;
                this.structureData.valid = false;
                break;
            }
            if (this.structureData.compareChanges()) this.notifyChange();
        }
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.structureData.deserializeNBT(registries, nbt.getCompound("StructureData"));
        this.energy.setEnergyStored(nbt.getInt("energy"));
        this.invalidateRenderBB();
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("StructureData", this.structureData.serializeNBT(registries));
        nbt.putInt("energy", this.energy.getEnergyStored());
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(
                Vec3.atLowerCornerOf(this.worldPosition.above()),
                Vec3.atLowerCornerWithOffset(
                        this.worldPosition.above(this.structureData.height+1),
                        1, 1, 1
                )
        );
    }

    public StructureData getStructureData() {
        return this.structureData;
    }

    public int getRedstoneSignal() {
        return 0;
    }

    public static class StructureData implements INBTSerializable<CompoundTag> {
        @Override
        public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("valid", this.valid);
            nbt.putInt("height", this.height);
            nbt.putInt("ne", this.ne);
            nbt.putInt("se", this.se);
            nbt.putInt("sw", this.sw);
            nbt.putInt("nw", this.nw);
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.valid = nbt.getBoolean("valid");
            this.height = nbt.getInt("height");
            this.ne = nbt.getInt("ne");
            this.se = nbt.getInt("se");
            this.sw = nbt.getInt("sw");
            this.nw = nbt.getInt("nw");
        }

        private void resetData() {
            this.oldHeight = this.height;
            this.oldNe = this.ne;
            this.oldSe = this.se;
            this.oldSw = this.sw;
            this.oldNw = this.nw;
            this.oldValid = this.valid;

            this.height = 0;
            this.ne = this.se = this.sw = this.nw = 0;
            this.valid = false;
        }

        public boolean compareChanges() {
            if (this.oldValid != this.valid) return true;
            if (this.oldHeight != this.height) return true;
            return this.oldNe != this.ne || this.oldSe != this.se || this.oldSw != this.sw || this.oldNw != this.nw;
        }

        private boolean oldValid;
        private int oldHeight;
        private int oldNe;
        private int oldSe;
        private int oldSw;
        private int oldNw;

        private boolean valid = false;
        private int height = 0;
        private int ne = 0;
        private int se = 0;
        private int sw = 0;
        private int nw = 0;

        private StructureData() {}

        public boolean isValid() {
            return this.valid;
        }
        public int getHeight() {
            return this.height;
        }

        public int getPowerConsumption() {
            return this.height + this.ne * 8 + this.se * 8 + this.sw * 8 + this.nw * 8 + 16;
        }
    }
}
