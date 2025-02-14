package nl.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public abstract class StructureBlockModifier {
    public abstract BlockState modifyBlock(BlockState original, Random random);
}
