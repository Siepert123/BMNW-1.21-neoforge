package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IRadioAntennaStructureBlock {
    Direction getFacing(BlockState state);
    boolean allowsArbitraryFacing();
}
