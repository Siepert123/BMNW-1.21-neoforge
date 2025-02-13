package com.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class BlockModifierGlassPaneDecay extends StructureBlockModifier {
    @Override
    public BlockState modifyBlock(BlockState original, Random random) {
        if (original.is(Blocks.GLASS_PANE) && random.nextBoolean()) return Blocks.AIR.defaultBlockState();
        return original;
    }
}
