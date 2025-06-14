package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class OptimizedBlockEntity extends BlockEntity {
    public OptimizedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateRenderBB();
    }

    public void invalidateRenderBB() {
        this.renderBB = null;
    }

    private AABB renderBB;

    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition);
    }
    public AABB getRenderBB() {
        if (this.renderBB == null) this.renderBB = this.createRenderBoundingBox();
        return this.renderBB;
    }
}
