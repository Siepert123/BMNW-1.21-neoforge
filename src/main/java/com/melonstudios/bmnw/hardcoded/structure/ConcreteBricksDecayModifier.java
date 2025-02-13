package com.melonstudios.bmnw.hardcoded.structure;

import com.melonstudios.bmnw.block.BMNWBlocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class ConcreteBricksDecayModifier extends StructureBlockModifier {
    private final float decay_chance;

    public ConcreteBricksDecayModifier(float decay_chance) {
        this.decay_chance = decay_chance;
    }

    @Override
    public BlockState modifyBlock(BlockState original, Random random) {
        if (original.is(BMNWBlocks.CONCRETE_BRICKS.get())) {
            if (random.nextFloat() < decay_chance) {
                int type = random.nextInt(2);
                if (type == 0) return BMNWBlocks.MOSSY_CONCRETE_BRICKS.get().defaultBlockState();
                if (type == 1) return BMNWBlocks.CRACKED_CONCRETE_BRICKS.get().defaultBlockState();
            }
        }
        return original;
    }
}
