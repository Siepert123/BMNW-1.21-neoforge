package com.siepert.bmnw.misc;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public interface IWorldGenerator {
    void generate(ChunkPos pos, Level level, Random random);
}
