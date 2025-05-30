package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import nl.melonstudios.bmnw.block.decoration.BaseSmallLampBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;

public class SmallLampBlockEntity extends BlockEntity {
    public SmallLampBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.SMALL_LAMP.get(), pos, blockState);
    }

    private AABB renderAABB = null;

    private void createRenderAABB() {
        this.renderAABB = new AABB(this.worldPosition);
    }

    public AABB getRenderAABB() {
        if (this.renderAABB == null) this.createRenderAABB();
        return this.renderAABB;
    }

    public void invalidateRenderAABB() {
        this.renderAABB = null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateRenderAABB();
    }

    public boolean shouldRender() {
        BlockState state = this.getBlockState();
        return state.getValue(BaseSmallLampBlock.POWERED) ^ ((BaseSmallLampBlock)state.getBlock()).inverted;
    }
}
