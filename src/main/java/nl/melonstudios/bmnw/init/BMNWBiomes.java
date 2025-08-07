package nl.melonstudios.bmnw.init;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.cfg.BMNWCommonConfig;

import java.util.ArrayList;
import java.util.List;

public class BMNWBiomes {
    public static Registry<Biome> biomeRegistry(Level level) {
        return level.registryAccess().registryOrThrow(Registries.BIOME);
    }

    public static final ResourceKey<Biome> VOLCANO_WASTES = ResourceKey.create(
            Registries.BIOME, BMNW.namespace("volcano_wastes")
    );
    public static Holder<Biome> volcano_wastes(Level level) {
        return biomeRegistry(level).getHolder(VOLCANO_WASTES).orElseThrow();
    }

    public static final ResourceKey<Biome> VOLCANO_WASTES_SEVERE = ResourceKey.create(
            Registries.BIOME, BMNW.namespace("volcano_wastes_severe")
    );
    public static Holder<Biome> volcano_wastes_severe(Level level) {
        return biomeRegistry(level).getHolder(VOLCANO_WASTES_SEVERE).orElseThrow();
    }

    public static void fillBiome(ServerLevel level, BlockPos start, BlockPos end, Holder<Biome> biome) {
        BoundingBox boundingBox = BoundingBox.fromCorners(start, end);

        List<ChunkAccess> chunks = new ArrayList<>();
        for (int k = SectionPos.blockToSectionCoord(boundingBox.minZ()); k <= SectionPos.blockToSectionCoord(boundingBox.maxZ()); k++) {
            for (int l = SectionPos.blockToSectionCoord(boundingBox.minX()); l <= SectionPos.blockToSectionCoord(boundingBox.maxX()); l++) {
                ChunkAccess chunk = level.getChunk(l, k, ChunkStatus.FULL, true);
                if (chunk != null) chunks.add(chunk);
                else {
                    if (!BMNWCommonConfig.suppressErrors()) throw new IllegalStateException("Could not get chunk at X:" + l + " Z:" + k);
                }
            }
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (ChunkAccess chunk : chunks) {
            chunk.fillBiomesFromNoise(
                    (x, y, z, sampler) -> {
                        mutable.set(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
                        if (!boundingBox.isInside(mutable)) {
                            return chunk.getNoiseBiome(x, y, z);
                        }
                        return biome;
                    },
                    level.getChunkSource().randomState().sampler()
            );
            chunk.setUnsaved(true);
        }

        level.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
    }
    public static void fillBiomeCylindrical(ServerLevel level, BlockPos origin, int upwards, int range, Holder<Biome> biome) {
        BoundingBox boundingBox = new BoundingBox(
                origin.getX()-range, origin.getY(), origin.getZ()-range,
                origin.getX()+range, origin.getY()+upwards, origin.getZ()+range
        );

        List<ChunkAccess> chunks = new ArrayList<>();
        for (int k = SectionPos.blockToSectionCoord(boundingBox.minZ()); k <= SectionPos.blockToSectionCoord(boundingBox.maxZ()); k++) {
            for (int l = SectionPos.blockToSectionCoord(boundingBox.minX()); l <= SectionPos.blockToSectionCoord(boundingBox.maxX()); l++) {
                ChunkAccess chunk = level.getChunk(l, k, ChunkStatus.FULL, true);
                if (chunk != null) chunks.add(chunk);
                else {
                    if (!BMNWCommonConfig.suppressErrors()) throw new IllegalStateException("Could not get chunk at X:" + l + " Z:" + k);
                }
            }
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int r2 = range*range;
        for (ChunkAccess chunk : chunks) {
            chunk.fillBiomesFromNoise(
                    (x, y, z, sampler) -> {
                        mutable.set(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
                        if (!boundingBox.isInside(mutable)) {
                            return chunk.getNoiseBiome(x, y, z);
                        }
                        if (mutable.set(QuartPos.toBlock(x), origin.getY(), QuartPos.toBlock(z)).distSqr(origin) > r2) {
                            return chunk.getNoiseBiome(x, y, z);
                        };
                        return biome;
                    },
                    level.getChunkSource().randomState().sampler()
            );
            chunk.setUnsaved(true);
        }

        level.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
    }
}
