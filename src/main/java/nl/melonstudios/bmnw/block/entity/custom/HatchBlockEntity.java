package nl.melonstudios.bmnw.block.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import nl.melonstudios.bmnw.block.BMNWBlocks;
import nl.melonstudios.bmnw.block.custom.HatchBlock;
import nl.melonstudios.bmnw.block.entity.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HatchBlockEntity extends BlockEntity {
    public HatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.HATCH.get(), pos, blockState);
        open = blockState.getValue(HatchBlock.OPEN);
    }

    public boolean open;
    public int ticks = 0;

    public void tick() {
        if (ticks > 0) ticks--;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("ticks", this.ticks);
        tag.putBoolean("open", this.open);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.ticks = tag.getInt("ticks");
        this.open = tag.getBoolean("open");
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
    public boolean isOpen() {
        return open;
    }

    public BlockState getOtherPart() {
        return BMNWBlocks.CONCRETE_ENCAPSULATED_LADDER.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, getFacing());
    }
}
