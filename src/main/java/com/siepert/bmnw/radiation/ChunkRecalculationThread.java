package com.siepert.bmnw.radiation;

import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.misc.BMNWAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkRecalculationThread implements Runnable {
    public ChunkRecalculationThread(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    public ChunkAccess chunk;
    public float result = 0.0f;
    public boolean working = true;
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

                    calculatedRads += HazardRegistry.getRadRegistry(state.getBlock());
                }
            }
        }

        chunk.setData(BMNWAttachments.SOURCE_RADIOACTIVITY, calculatedRads);
        chunk.setData(BMNWAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, true);

        chunk = null;

        result = calculatedRads;

        working = false;
    }
}
