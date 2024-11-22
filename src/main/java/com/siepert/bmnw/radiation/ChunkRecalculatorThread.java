package com.siepert.bmnw.radiation;

import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import com.siepert.bmnw.misc.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import static com.siepert.bmnw.radiation.RadHelper.getInsertedRadiation;

public class ChunkRecalculatorThread implements Runnable {
    public ChunkRecalculatorThread(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    public final ChunkAccess chunk;
    public float result = 0.0f;
    @Override
    public void run() {
        Level level = chunk.getLevel();
        if (level == null || level.isClientSide()) return;

        float calculatedRads = 0.0f;

        for (int y = level.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            if (chunk.isYSpaceEmpty(y, y)) continue;

            for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
                for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.getBlock() instanceof IRadioactiveBlock block) {
                        calculatedRads += getInsertedRadiation(level, pos, block.getRadioactivity(level, pos, state));
                    }
                }
            }
        }

        chunk.setData(ModAttachments.SOURCE_RADIOACTIVITY, calculatedRads);
        chunk.setData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, true);

        result = calculatedRads;
    }
}
