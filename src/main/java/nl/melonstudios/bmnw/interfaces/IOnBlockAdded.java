package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public interface IOnBlockAdded {
    void onBlockAdded(LevelAccessor level, BlockPos pos);
}
