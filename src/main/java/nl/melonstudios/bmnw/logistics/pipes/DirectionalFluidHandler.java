package nl.melonstudios.bmnw.logistics.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record DirectionalFluidHandler(BlockPos pos, Direction face) {
}
