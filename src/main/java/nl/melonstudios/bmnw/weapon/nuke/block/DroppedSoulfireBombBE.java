package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import nl.melonstudios.bmnw.block.entity.OptimizedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DroppedSoulfireBombBE extends OptimizedBlockEntity {
    public DroppedSoulfireBombBE(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.DROPPED_SOULFIRE_BOMB.get(), pos, blockState);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(1);
    }
}
