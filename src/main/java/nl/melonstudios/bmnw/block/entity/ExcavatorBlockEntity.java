package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;

public class ExcavatorBlockEntity extends BlockEntity {
    public ExcavatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.EXCAVATOR.get(), pos, blockState);
    }

    private void tick() {

    }

    public static void tick(ExcavatorBlockEntity be) {
        be.tick();
    }
}
