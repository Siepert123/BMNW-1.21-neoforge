package nl.melonstudios.bmnw.hardcoded.lootpool;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

@FunctionalInterface
public interface RandomStateSupplier {
    BlockState supply(Random random);
}
