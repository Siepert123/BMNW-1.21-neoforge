package nl.melonstudios.bmnw.logistics.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

public interface IPropagatesPipeNet {
    default boolean isDisabled(Level level, BlockPos pos, BlockState state) {
        return false;
    }
    Collection<BlockPos> getConnectedPositions(Level level, BlockPos pos, BlockState state);
    Collection<DirectionalFluidHandler> getConnectedFluidHandlers(Level level, BlockPos pos, BlockState state);
    void recalculateConnections(Level level, BlockPos pos, BlockState state);
}
