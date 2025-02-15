package nl.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Structures {
    public static final Logger LOGGER = LogManager.getLogger("BMNW Structures");
    public static Collection<ResourceLocation> getAllStructureResourceLocations() {
        return STRUCTURE_REGISTRY.keySet();
    }

    private static final Map<ResourceLocation, StructureData> STRUCTURE_REGISTRY = new HashMap<>();

    public static void registerStructure(ResourceLocation id, StructureData structure) {
        if (STRUCTURE_REGISTRY.containsKey(id)) {
            LOGGER.warn("Skipped structure with id {} because that id was taken", id);
        } else {
            STRUCTURE_REGISTRY.put(id, structure);
            LOGGER.info("Registered structure {}", id);
        }
    }
    public static void registerStructure(String id, StructureData structure) {
        registerStructure(ResourceLocation.parse(id), structure);
    }

    private static Random createRandom(ChunkPos pos, int salt, long seed) {
        return new Random(
                (((long) pos.hashCode()) | (long) salt << 16) ^ seed
        );
    }
    public static void tryGenerate(LevelAccessor level, ChunkPos pos, long seed) {
        if (!validCache) LOGGER.warn("Warning: seedCache is invalid, structure generation may mismatch with true results!");
        boolean placed = false;
        for (Map.Entry<ResourceLocation, StructureData> entry : STRUCTURE_REGISTRY.entrySet()) {
            ResourceLocation id = entry.getKey();
            StructureData structure = entry.getValue();
            Random random = createRandom(pos, structure.spawningLogic.getSalt(), seed);
            if (structure.spawningLogic.canSpawn(level, pos, random) && (!placed || structure.spawningLogic.allowMultipleStructures())) {
                if (!structure.structure.place(level, pos, random, structure.blockModifiers)) {
                    LOGGER.warn("Could not place structure {} at chunk x:{} z:{}", id, pos.x, pos.z);
                } else placed = true;
            }
        }
    }
    public static boolean structurePotentiallyInChunk(Level level, ChunkPos pos, ResourceLocation id) {
        if (!validCache) LOGGER.warn("Warning: seedCache is invalid, may yield unexpected results!");
        StructureData structure = STRUCTURE_REGISTRY.get(id);
        Random random = createRandom(pos, structure.spawningLogic.getSalt(), seedCache);
        return structure.spawningLogic.canSpawn(level, pos, random);
    }

    public static long seedCache = 0;
    public static boolean validCache = false;
}
