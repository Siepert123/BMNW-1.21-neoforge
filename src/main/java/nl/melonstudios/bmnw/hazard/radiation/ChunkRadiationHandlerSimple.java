package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Shrimple radiation handler.
 *
 * @author HBM the bobcat
 */
public class ChunkRadiationHandlerSimple extends ChunkRadiationHandler {
    private static final float max_rads = 100_000f;

    private HashMap<Level, SimpleRadiationPerWorld> perWorld = new HashMap<>();

    @Override
    public void updateSystem() {
        for (Map.Entry<Level, SimpleRadiationPerWorld> entry : perWorld.entrySet()) {
            HashMap<ChunkPos, Float> radiation = entry.getValue().radiation;
            HashMap<ChunkPos, Float> buff = new HashMap<>(radiation);
            radiation.clear();
            Level level = entry.getKey();

            for (Map.Entry<ChunkPos, Float> chunk : buff.entrySet()) {
                if (chunk.getValue() == 0) continue;

                ChunkPos pos = chunk.getKey();

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int type = Math.abs(i) + Math.abs(j);
                        float percent = type == 0 ? 0.6f : type == 1 ? 0.075f : 0.025f;
                        ChunkPos newPos = new ChunkPos(pos.x + i, pos.z + j);

                        if (buff.containsKey(newPos)) {
                            Float f = radiation.get(newPos);
                            float rad = f == null ? 0 : f;
                            float newRad = rad + chunk.getValue() * percent;
                            newRad = Mth.clamp(0, newRad * 0.99f - 0.05f, max_rads);
                            radiation.put(newPos, newRad);
                        } else {
                            radiation.put(newPos, chunk.getValue() * percent);
                        }

                        float rads = radiation.get(newPos);
                        if (rads > BMNW.Constants.evil_fog_rads &&
                                level != null &&
                                level.random.nextInt(BMNW.Constants.evil_fog_chance) == 0 &&
                                level.getChunkSource().hasChunk(newPos.x, newPos.z)) {
                            int x = newPos.getBlockX(level.random.nextInt(16));
                            int z = newPos.getBlockZ(level.random.nextInt(16));
                            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + level.random.nextInt(5);

                            if (level instanceof ServerLevel) {
                                ((ServerLevel)level).sendParticles(BMNWParticleTypes.EVIL_FOG.get(), x + 0.5, y + 0.5, z + 0.5,
                                        1, 0, 0, 0, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getRadiation(Level level, BlockPos pos) {
        SimpleRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) {
            ChunkPos cp = new ChunkPos(pos);
            Float rads = radWorld.radiation.get(cp);
            return rads == null ? 0 : Mth.clamp(rads, 0, max_rads);
        }

        return 0;
    }

    @Override
    public void setRadiation(Level level, BlockPos pos, float rads) {
        SimpleRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) {
            if (level.isInWorldBounds(pos)) {
                ChunkPos cp = new ChunkPos(pos);
                radWorld.radiation.put(cp, rads);
                level.getChunk(pos).setUnsaved(true);
            }
        }
    }

    @Override
    public void increaseRadiation(Level level, BlockPos pos, float rads) {
        setRadiation(level, pos, getRadiation(level, pos) + rads);
    }

    @Override
    public void decreaseRadiation(Level level, BlockPos pos, float rads) {
        setRadiation(level, pos, Math.max(getRadiation(level, pos) - rads, 0));
    }

    @Override
    public void clearSystem(Level level) {
        SimpleRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) {
            radWorld.radiation.clear();
        }
    }

    @Override
    public void onWorldLoad(LevelEvent.Load event) {
        try {
            if (!event.getLevel().isClientSide()) {
                perWorld.put((Level) event.getLevel(), new SimpleRadiationPerWorld());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWorldUnload(LevelEvent.Unload event) {
        try {
            if (!event.getLevel().isClientSide()) {
                perWorld.remove((Level) event.getLevel());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private static final String NBT_KEY_CHUNK_RADIATION = "bmnw_simple_radiation";

    @Override
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (event.getLevel() != null && !event.getLevel().isClientSide()) {
            SimpleRadiationPerWorld radWorld = perWorld.get((Level)event.getLevel());

            if (radWorld != null) {
                radWorld.radiation.put(event.getChunk().getPos(), event.getData().getFloat(NBT_KEY_CHUNK_RADIATION));
            }
        }
    }

    @Override
    public void onChunkSave(ChunkDataEvent.Save event) {
        if (event.getLevel() != null && !event.getLevel().isClientSide()) {
            SimpleRadiationPerWorld radWorld = perWorld.get((Level)event.getLevel());

            if (radWorld != null) {
                Float f = radWorld.radiation.get(event.getChunk().getPos());
                float rads = f == null ? 0 : f;
                event.getData().putFloat(NBT_KEY_CHUNK_RADIATION, rads);
            }
        }
    }

    @Override
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() != null && !event.getLevel().isClientSide()) {
            SimpleRadiationPerWorld radWorld = perWorld.get((Level) event.getLevel());

            if (radWorld != null) {
                radWorld.radiation.remove(event.getChunk().getPos());
            }
        }
    }

    public static class SimpleRadiationPerWorld {
        public HashMap<ChunkPos, Float> radiation = new HashMap<>();
    }

    @Override
    public void handleWorldDestruction() {
        int count = 10;
        int threshold = 10;
        int chunks = 5;

        for (Map.Entry<Level, SimpleRadiationPerWorld> entry : perWorld.entrySet()) {
            Level level = entry.getKey();
            SimpleRadiationPerWorld list = entry.getValue();

            Object[] entries = list.radiation.entrySet().toArray();

            if (entries.length == 0) continue;

            for (int c = 0; c < chunks; c++) {
                Map.Entry<ChunkPos, Float> randEnt = (Map.Entry<ChunkPos, Float>) entries[level.random.nextInt(entries.length)];

                ChunkPos pos = randEnt.getKey();
                ServerLevel serverLevel = (ServerLevel) level;
                ServerChunkCache chunkCache = serverLevel.getChunkSource();

                for (int i = 0; i < count; i++) {
                    if (randEnt == null || randEnt.getValue() < threshold) continue;

                    if (chunkCache.hasChunk(pos.x, pos.z)) {
                        for (int a = 0; a < 16; a++) {
                            for (int b = 0; b < 16; b++) {
                                if (level.random.nextInt(3) != 0) continue;

                                int x = pos.getBlockX(a);
                                int z = pos.getBlockZ(b);
                                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - level.random.nextInt(2);

                                BlockPos origin = new BlockPos(x, y, z);

                                if (level.getBlockState(origin).is(Blocks.GRASS_BLOCK)) {
                                    level.setBlock(origin, BMNWBlocks.IRRADIATED_GRASS_BLOCK.get().defaultBlockState(), 3);
                                } else if (level.getBlockState(origin).is(Blocks.TALL_GRASS)) {
                                    level.setBlock(origin, Blocks.AIR.defaultBlockState(), 3);
                                } else if (level.getBlockState(origin).is(BlockTags.LEAVES) && !level.getBlockState(origin).is(BMNWBlocks.IRRADIATED_LEAVES.get())) {
                                    if (level.random.nextInt(7) <= 5) {
                                        level.setBlock(origin, BMNWBlocks.IRRADIATED_LEAVES.get().defaultBlockState(), 3);
                                    } else level.setBlock(origin, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
