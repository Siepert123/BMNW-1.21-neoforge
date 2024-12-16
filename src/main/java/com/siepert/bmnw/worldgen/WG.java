package com.siepert.bmnw.worldgen;

import com.siepert.bmnw.misc.IWorldGenerator;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated(forRemoval = true, since = "always")
public class WG {
    public static final List<IWorldGenerator> generators = new ArrayList<>();

    public static final List<ChunkAccess> chunksToGenerate = new ArrayList<>();

    @SuppressWarnings("all")
    public static void generate(ChunkAccess chunk) {
        long seed = ObfuscationReflectionHelper.getPrivateValue(BiomeManager.class, chunk.getLevel().getBiomeManager(),
                "biomeZoomSeed");
        for (IWorldGenerator generator : generators) {
            generator.generate(chunk.getPos(), chunk.getLevel(), new Random(chunk.getPos().hashCode() + seed));
        }
    }
}
