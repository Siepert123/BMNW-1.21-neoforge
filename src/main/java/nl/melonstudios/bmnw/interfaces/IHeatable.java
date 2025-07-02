package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IHeatable {
    /**
     * @param level Level
     * @param pos Block position
     * @param state Block state
     * @return Whether heat can be accepted at all
     */
    boolean canAcceptHeat(Level level, BlockPos pos, BlockState state);

    /**
     * @param level Level
     * @param pos Block position
     * @param state Block state
     * @param amount The amount of heat to be inserted
     * @return The amount of heat that was actually absorbed
     */
    int acceptHeat(Level level, BlockPos pos, BlockState state, int amount);
}
