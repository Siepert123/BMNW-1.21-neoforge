package com.siepert.bmnw.worldgen;

import com.siepert.bmnw.misc.IWorldGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class TestGen implements IWorldGenerator {
    @Override
    public void generate(ChunkPos pos, Level level, Random random) {
        for (int y = 0; y < 128; y++) {
            level.setBlock(pos.getBlockAt(0, y, 0), Blocks.BEDROCK.defaultBlockState(), 0);
        }
    }
}
