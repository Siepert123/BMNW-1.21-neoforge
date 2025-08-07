package nl.melonstudios.bmnw.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface RemoteActivateable {
    boolean onRemoteActivation(Level level, BlockPos pos);
}
