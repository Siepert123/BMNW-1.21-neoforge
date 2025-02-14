package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public interface IRadioactiveBlock {
    default float getRadioactivity(Level level, BlockPos pos, BlockState state) {
        return getRadioactivity();
    }
    default float getRadioactivity() {
        return 0;
    }
}
