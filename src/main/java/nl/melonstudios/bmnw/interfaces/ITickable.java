package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ITickable {
    void update();

    static <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<T> type) {
        return (l, p, s, be) -> ((ITickable)be).update();
    }
}
