package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.block.entity.OptimizedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;

public class DroppedSoulfireBombBE extends OptimizedBlockEntity {
    public DroppedSoulfireBombBE(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.DROPPED_SOULFIRE_BOMB.get(), pos, blockState);
    }
}
