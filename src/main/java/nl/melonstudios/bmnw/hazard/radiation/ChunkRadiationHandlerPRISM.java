package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.init.BMNWTags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The following text was made by HBM the bobcat himself:
 * <p>
 * The PRISM system aims to make a semi-realistic containment system with simplified and variable resistance values.
 * The general basis for this system is the simplified 3D system with its 16x16x16 regions, but in addition to those
 * sub-chunks, each sub-chunk has several arrays of resistance values (three arrays, one for each axis) where each
 * value represents the resistance of one "slice" of the sub-chunk. This allows resistances to be handled differently
 * depending on the direction the radiation is coming from, and depending on the sub-chunk's localized block resistance
 * density. While not as accurate as the pocket-based system from 1.12, it does a better job at simulating resistances
 * of various block types instead of a binary sealing/not sealing system. For example it is therefore possible to
 * safely store radioactive waste in a cave, shielded by many layers of rock and dirt, without needing extra concrete.
 * The system's name stems from the "gradient"-like handling of the resistance values per axis, multiple color
 * gradients make a rainbow, and rainbows come from prisms. Just like a prism, sub-chunks too handle the radiation
 * going through them differently depending on the angle of approach.
 * <p>
 * <p>
 * This nice ASCII art breaks in display :(
 * <p>
 *      ___
 *     /\  \
 *    /  \  \
 *   /    \  \
 *  /      \  \
 * /________\__\
 *
 * @author HBM the bobcat
 *
 */
@SuppressWarnings("all") //i do not care!!!
public class ChunkRadiationHandlerPRISM extends ChunkRadiationHandler {
    public static final Logger LOGGER = LogManager.getLogger("Radiation System PRISM");

    public ConcurrentHashMap<Level, RadPerWorld> perWorld = new ConcurrentHashMap<>();
    public static int cycles = 0;

    public static final float MAX_RADIATION = 1_000_000;
    private static final String NBT_KEY_CHUNK_RADIATION = "bmnw_prism_radiation_";
    private static final String NBT_KEY_CHUNK_RESISTANCE = "bmnw_prism_resistance_";
    private static final String NBT_KEY_CHUNK_EXISTS = "bmnw_prism_exists_";

    @Override
    public void updateSystem() {
        cycles++;

        for (ServerLevel level : server.getAllLevels()) {
            level.getProfiler().push("radiation update system");
            RadPerWorld system = perWorld.get(level);

            if (system == null) continue;

            int rebuildAllowance = 25;

            for (Map.Entry<ChunkPos, SubChunk[]> entry : system.radiation.entrySet()) {
                ChunkPos pos = entry.getKey();

                int sectionCount = level.getSectionsCount();
                int minSection = level.getMinSection();

                for (int i = 0; i < sectionCount; i++) {
                    SubChunk sub = entry.getValue()[i];

                    boolean triedRebuild = false;

                    if (sub != null) {
                        sub.prevRadiation = sub.radiation;
                        sub.radiation = 0;

                        if (rebuildAllowance > 0 && sub.needsRebuild) {
                            sub.rebuild(level, pos.getBlockAt(0, (i + minSection) << 4, 0));
                            if (!sub.needsRebuild) {
                                rebuildAllowance--;
                                triedRebuild = true;
                            }
                        }

                        /* This sucks, actually
                        if (!triedRebuild && Math.abs(pos.x * pos.z) % 5 == cycles % 5 && level.isLoaded(pos.getMiddleBlockPosition((i-4) << 4))) {
                            LevelChunk chunk = level.getChunk(pos.x, pos.z);
                            LevelChunkSection section = chunk.getSection(i);
                            int checksum = 0;

                            if (section != null) {
                                for (int iX = 0; iX < 16; iX++)
                                    for (int iY = 0; iY < 16; iY++)
                                        for (int iZ = 0; iZ < 16; iZ++)
                                            checksum += section.getBlockState(iX, iY, iZ).hashCode();
                            }

                            if (checksum != sub.checksum) {
                                sub.rebuild(level, pos.getBlockAt(0, (i + minSection) << 4, 0));
                            }
                        }
                        */
                    }
                }
            }

            Iterator<Map.Entry<ChunkPos, SubChunk[]>> it = system.radiation.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ChunkPos, SubChunk[]> chunk = it.next();
                if (getPreviousChunkRadiation(chunk.getValue()) <= 0) {
                    level.getProfiler().pop();
                    continue;
                }

                int sectionCount = level.getSectionsCount();
                int minSection = level.getMinSection();

                for (int i = 0; i < sectionCount; i++) {
                    SubChunk sub = chunk.getValue()[i];

                    if (sub != null) {
                        if (sub.prevRadiation <= 0 || Float.isNaN(sub.prevRadiation) || Float.isInfinite(sub.prevRadiation)) {
                            level.getProfiler().pop();
                            continue;
                        }
                        float radSpread = 0;
                        for (Direction direction : Direction.values()) {
                            radSpread += spreadRadiation(level, sub, i, chunk.getKey(), chunk.getValue(), system.radiation, direction);
                        }
                        sub.radiation += (sub.prevRadiation - radSpread) * 0.95f;
                        sub.radiation -= 1;
                        sub.radiation = Mth.clamp(sub.radiation, 0, MAX_RADIATION);
                    }
                }
            }

            system.radiation.putAll(newAdditions);
            newAdditions.clear();
            level.getProfiler().pop();
        }
    }


    @Override
    public void notifyBlockChange(Level level, BlockPos pos) {
        RadPerWorld system = perWorld.get(level);

        if (system != null) {
            ChunkPos cp = new ChunkPos(pos);
            SubChunk[] chunk = system.radiation.get(cp);

            if (chunk != null) {
                int sectionCount = level.getSectionsCount();
                int minSection = level.getMinSection();

                int yReg = Mth.clamp(pos.getY() >> 4, minSection, sectionCount+minSection) - minSection;

                SubChunk sub = chunk[yReg];

                if (sub == null) {
                    sub = new SubChunk();
                    chunk[yReg] = sub;
                }
                sub.needsRebuild = true;
            }
        }
    }

    private static float spreadRadiation(Level level, SubChunk source, int y, ChunkPos origin, SubChunk[] chunk,
                                         ConcurrentHashMap<ChunkPos, SubChunk[]> map, Direction direction) {
        level.getProfiler().push("radiation spread");
        float spread = 0.1f;
        float amount = source.prevRadiation * spread;

        if (amount <= 1) {
            level.getProfiler().pop();
            return 0;
        }

        int sectionCount = level.getSectionsCount();
        int minSection = level.getMinSection();

        if (direction.getStepY() != 0) {
            if (direction == Direction.UP && y == sectionCount-1) {
                level.getProfiler().pop();
                return amount;
            }
            if (direction == Direction.DOWN && y == 0) {
                level.getProfiler().pop();
                return amount;
            }
            if (chunk[y + direction.getStepY()] == null)
                chunk[y + direction.getStepY()] =
                        new SubChunk().rebuild(level, origin.getBlockAt(0, (y+minSection) << 4, 0));
            SubChunk to = chunk[y + direction.getStepY()];
            level.getProfiler().pop();
            return spreadRadiationTo(source, to, amount, direction);
        } else {
            ChunkPos newPos = new ChunkPos(origin.x+direction.getStepX(), origin.z+direction.getStepZ());
            if (!level.isLoaded(newPos.getMiddleBlockPosition((y+minSection) << 4))) {
                level.getProfiler().pop();
                return amount;
            }
            SubChunk[] newChunk = map.get(newPos);
            if (newChunk == null) {
                newChunk = new SubChunk[sectionCount];
                newAdditions.put(newPos, newChunk);
            }
            if (newChunk[y] == null)
                newChunk[y] = new SubChunk().rebuild(level, newPos.getBlockAt(0, (y+minSection) << 4, 0));
            SubChunk to = newChunk[y];
            level.getProfiler().pop();
            return spreadRadiationTo(source, to, amount, direction);
        }
    }

    private static float spreadRadiationTo(SubChunk from, SubChunk to, float amount, Direction direction) {
        float resistance = from.getResistanceValue(direction.getOpposite()) + to.getResistanceValue(direction);
        double fun = Math.pow(Math.E, -resistance / 10_000.0);
        float toMove = (float) Math.min(amount * fun, amount);
        to.radiation += toMove;
        return toMove;
    }

    private static float getPreviousChunkRadiation(SubChunk[] chunk) {
        float rad = 0;
        for(SubChunk sub : chunk)
            if(sub != null)
                rad += sub.prevRadiation; return rad;
    }

    @Override
    public float getRadiation(Level level, BlockPos pos) {
        level.getProfiler().push("radiation get");
        RadPerWorld system = perWorld.get(level);

        int sectionCount = level.getSectionsCount();
        int minSection = level.getMinSection();

        if (system != null) {
            ChunkPos cp = new ChunkPos(pos);
            int yReg = Mth.clamp(pos.getY() >> 4, minSection, sectionCount+minSection-1) - minSection;
            SubChunk[] subChunks = system.radiation.get(cp);
            if (subChunks != null) {
                SubChunk rad = subChunks[yReg];
                if (rad != null) {
                    level.getProfiler().pop();
                    return rad.radiation;
                }
            }
        }
        level.getProfiler().pop();
        return 0;
    }

    @Override
    public void setRadiation(Level level, BlockPos pos, float rads) {
        level.getProfiler().push("radiation set");
        if (Float.isNaN(rads)) rads = 0;

        RadPerWorld system = perWorld.get(level);

        int sectionCount = level.getSectionsCount();
        int minSection = level.getMinSection();

        if (system != null) {
            ChunkPos cp = new ChunkPos(pos);
            int yReg = Mth.clamp(pos.getY() >> 4, minSection, sectionCount+minSection-1) - minSection;
            SubChunk[] subChunks = system.radiation.computeIfAbsent(cp, k -> new SubChunk[sectionCount]);
            if (subChunks[yReg] == null) subChunks[yReg] = new SubChunk().rebuild(level, pos);
            subChunks[yReg].radiation = Mth.clamp(rads, 0, MAX_RADIATION);
            level.getChunk(pos).setUnsaved(true);
        }
        level.getProfiler().pop();
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
    public void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            perWorld.put((Level)event.getLevel(), new RadPerWorld());
        }
    }

    @Override
    public void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            perWorld.remove((Level)event.getLevel());
        }
    }

    @Override
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            Level level = (Level) event.getLevel();
            level.getProfiler().push("radiation chunk load");
            ChunkPos cp = event.getChunk().getPos();
            RadPerWorld system = perWorld.get(level);

            if (system != null && !system.radiation.containsKey(cp)) {
                system.uncheckedChunks.add(cp);
                /* Let's see if this fixes anything
                int sectionCount = event.getLevel().getSectionsCount();
                int minSection = event.getLevel().getMinSection();

                SubChunk[] chunk = new SubChunk[sectionCount];

                for (int i = 0; i < sectionCount; i++) {
                    chunk[i] = new SubChunk().rebuild(event.getLevel(), cp.getBlockAt(
                            0, (i + minSection) << 4, 0
                    ), event.getChunk(), true);
                }

                system.radiation.put(cp, chunk);
                */
            }
            level.getProfiler().pop();
        }
    }

    @Override
    public void onWorldTick(ServerTickEvent event) {
        for (Map.Entry<Level, RadPerWorld> entry : perWorld.entrySet()) {
            RadPerWorld system = entry.getValue();
            Level level = entry.getKey();

            level.getProfiler().push("radiation tick");
            final int sectionCount = level.getSectionsCount();
            final int minSection = level.getMinSection();

            while (!system.uncheckedChunks.isEmpty()) {
                ChunkPos cp = system.uncheckedChunks.removeFirst();
                if (!system.radiation.containsKey(cp)) {

                    SubChunk[] chunk = new SubChunk[sectionCount];

                    for (int i = 0; i < sectionCount; i++) {
                        chunk[i] = new SubChunk().rebuild(level, cp.getBlockAt(
                                0, (i + minSection) << 4, 0
                        ), level.getChunk(cp.x, cp.z), true);
                    }

                    system.radiation.put(cp, chunk);
                }
            }
            level.getProfiler().pop();
        }
    }

    @Override
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            ChunkPos cp = event.getChunk().getPos();
            RadPerWorld system = perWorld.get((Level)event.getLevel());

            int sectionCount = event.getLevel().getSectionsCount();
            int minSection = event.getLevel().getMinSection();

            if (system != null) {
                SubChunk[] chunk = new SubChunk[sectionCount];

                for (int i = 0; i < sectionCount; i++) {
                    if (!event.getData().getBoolean(NBT_KEY_CHUNK_EXISTS + i)) {
                        chunk[i] = new SubChunk().rebuild(event.getLevel(), cp.getBlockAt(
                                0, (i + minSection) << 4, 0
                        ), event.getChunk(), true);
                    } else {
                        SubChunk sub = new SubChunk();
                        chunk[i] = sub;
                        sub.radiation = event.getData().getFloat(NBT_KEY_CHUNK_RADIATION + i);
                        for (int j = 0; j < 16; j++)
                            sub.xResist[j] = event.getData().getFloat(NBT_KEY_CHUNK_RESISTANCE + "x_" + j + "_" + i);
                        for (int j = 0; j < 16; j++)
                            sub.yResist[j] = event.getData().getFloat(NBT_KEY_CHUNK_RESISTANCE + "y_" + j + "_" + i);
                        for (int j = 0; j < 16; j++)
                            sub.zResist[j] = event.getData().getFloat(NBT_KEY_CHUNK_RESISTANCE + "z_" + j + "_" + i);
                    }
                }

                system.radiation.put(event.getChunk().getPos(), chunk);
            }
        }
    }

    @Override //NOTE: This lags out the game for some reason.
    public void onChunkSave(ChunkDataEvent.Save event) {
        Level level = (Level)event.getLevel();
        if (!level.isClientSide()) {
            RadPerWorld system = perWorld.get(level);

            int sectionCount = level.getSectionsCount();

            if (system != null) {
                SubChunk[] chunk = system.radiation.get(event.getChunk().getPos());
                if (chunk != null) {
                    for (int i = 0; i < sectionCount; i++) {
                        SubChunk sub = chunk[i];
                        if (sub != null) {
                            float rads = sub.radiation;
                            event.getData().putFloat(NBT_KEY_CHUNK_RADIATION + i, rads);
                            for(int j = 0; j < 16; j++) event.getData().putFloat(NBT_KEY_CHUNK_RESISTANCE + "x_" + j + "_" + i, sub.xResist[j]);
                            for(int j = 0; j < 16; j++) event.getData().putFloat(NBT_KEY_CHUNK_RESISTANCE + "y_" + j + "_" + i, sub.yResist[j]);
                            for(int j = 0; j < 16; j++) event.getData().putFloat(NBT_KEY_CHUNK_RESISTANCE + "z_" + j + "_" + i, sub.zResist[j]);
                            event.getData().putBoolean(NBT_KEY_CHUNK_EXISTS + i, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onChunkUnload(ChunkEvent.Unload event) {
        Level level = (Level)event.getLevel();
        if (!level.isClientSide()) {
            RadPerWorld system = perWorld.get(level);
            if (system != null) {
                system.radiation.remove(event.getChunk().getPos());
            }
        }
    }

    private static final float
            min_rads_3 = 10000,
            min_rads_2 = 100;

    @Override
    public void handleWorldDestruction() {
        int count = 10;
        int threshold = 10;
        int chunks = 5;

        for (Map.Entry<Level, RadPerWorld> entry : perWorld.entrySet()) {
            Level level = entry.getKey();
            RadPerWorld list = entry.getValue();

            Object[] entries = list.radiation.entrySet().toArray();

            if (entries.length == 0) continue;

            int sectionsCount = level.getSectionsCount();
            int minSection = level.getMinSection();

            for (int c = 0; c < chunks; c++) {
                Map.Entry<ChunkPos, SubChunk[]> randEnt = (Map.Entry<ChunkPos, SubChunk[]>) entries[level.random.nextInt(entries.length)];

                ChunkPos pos = randEnt.getKey();

                for (int i = 0; i < count; i++) {
                    for (int y = 0; y < sectionsCount; y++) {
                        if (randEnt.getValue() == null || randEnt.getValue()[y] == null || randEnt.getValue()[y].radiation < threshold)
                            continue;

                        if (level.isLoaded(pos.getBlockAt(0, (y + minSection) << 4, 0))) {
                            int realY = (y + minSection) << 4;
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    for (int yH = 0; yH < 16; yH++) {
                                        if (level.random.nextInt(3) != 0) continue;

                                        BlockPos bp = pos.getBlockAt(x, realY + yH, z);

                                        BlockState state = level.getBlockState(bp);

                                        if (state.is(BMNWTags.Blocks.IRRADIATABLE_GRASS_BLOCKS)) {
                                            level.setBlock(bp, BMNWBlocks.IRRADIATED_GRASS_BLOCK.get().defaultBlockState(), 2);
                                        } else if (state.is(BMNWTags.Blocks.IRRADIATABLE_PLANTS)) {
                                            level.setBlock(bp, BMNWBlocks.IRRADIATED_PLANT.get().defaultBlockState(), 2);
                                        } else if (state.is(BlockTags.LEAVES) && !state.is(BMNWBlocks.IRRADIATED_LEAVES.get())) {
                                            if (level.random.nextInt(7) <= 5) {
                                                level.setBlock(bp, BMNWBlocks.IRRADIATED_LEAVES.get().defaultBlockState(), 2);
                                            } else level.setBlock(bp, Blocks.AIR.defaultBlockState(), 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int x = level.random.nextInt(16);
                int z = level.random.nextInt(16);

                BlockPos bp = pos.getBlockAt(x, 0, z);
                bp = new BlockPos(bp.getX(), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bp.getX(), bp.getZ()), bp.getZ());

                int ySection = Mth.clamp(bp.getY() >> 4, minSection, sectionsCount+minSection)-minSection;

                if (level instanceof ServerLevel serverLevel &&
                        randEnt.getValue()[ySection] != null &&
                        randEnt.getValue()[ySection].radiation > BMNW.Constants.evil_fog_rads &&
                        level.random.nextInt(BMNW.Constants.evil_fog_chance) == 0) {
                    serverLevel.sendParticles(BMNWParticleTypes.EVIL_FOG.get(), bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5,
                            1, 0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void clearSystem(Level level) {
        RadPerWorld system = perWorld.get(level);

        if (system != null) system.radiation.clear();
    }

    public static final HashMap<ChunkPos, SubChunk[]> newAdditions = new HashMap<>();

    public static class RadPerWorld {
        public ConcurrentHashMap<ChunkPos, SubChunk[]> radiation = new ConcurrentHashMap<>();
        public List<ChunkPos> uncheckedChunks = new ArrayList<>();
    }

    public static class SubChunk {
        public float prevRadiation;
        public float radiation;
        public float[] xResist = new float[16];
        public float[] yResist = new float[16];
        public float[] zResist = new float[16];
        public boolean needsRebuild = false;
        public int checksum = 0;

        @Deprecated
        public void updateBlock(Level level, BlockPos pos) {

            if (!level.isLoaded(pos)) return;

            int sectionsCount = level.getSectionsCount();
            int minSection = level.getMinSection();

            int cX = pos.getX() >> 4;
            int cY = Mth.clamp(pos.getY(), minSection, sectionsCount + minSection - 1) - minSection;
            int cZ = pos.getZ() >> 4;

            int tX = cX << 4;
            int tY = cY << 4;
            int tZ = cZ << 4;

            int sX = Mth.clamp(pos.getX() - tX, 0, 15);
            int sY = Mth.clamp(pos.getY() - tY, 0, 15);
            int sZ = Mth.clamp(pos.getZ() - tZ, 0, 15);

            LevelChunk chunk = level.getChunk(cX, cZ);
            LevelChunkSection subChunk = chunk.getSection(cY);

            xResist[sX] = yResist[sY] = zResist[sZ] = 0;

            for (int iX = 0; iX < 16; iX++) {
                for(int iY = 0; iY < 16; iY ++) {
                    for(int iZ = 0; iZ < 16; iZ ++) {
                        if(iX == sX || iY == sY || iZ == sZ) { //only redo the three affected slices by this position change
                            BlockState state = subChunk.getBlockState(iX, iY, iZ);
                            if (state.isAir()) continue;
                            float resistance = state.getBlock().getExplosionResistance();
                            if (iX == sX) xResist[iX] += resistance;
                            if (iY == sY) xResist[iY] += resistance;
                            if (iZ == sZ) xResist[iZ] += resistance;
                        }
                    }
                }
            }
        }

        public SubChunk rebuild(LevelAccessor level, BlockPos pos) {
            return this.rebuild(level, pos, level.getChunk(pos));
        }
        public SubChunk rebuild(LevelAccessor level, BlockPos pos, ChunkAccess chunk) {
            return this.rebuild(level, pos, chunk, false);
        }
        public SubChunk rebuild(LevelAccessor level, BlockPos pos, ChunkAccess chunk, boolean enforce) {
            needsRebuild = true;

            if (!enforce && !level.isAreaLoaded(pos, 1)) return this;

            int sectionsCount = level.getSectionsCount();
            int minSection = level.getMinSection();

            int cX = pos.getX() >> 4;
            int cY = Mth.clamp(pos.getY() >> 4, minSection, sectionsCount + minSection - 1) - minSection;
            int cZ = pos.getZ() >> 4;

            int tX = cX << 4;
            int tY = cY << 4;
            int tZ = cZ << 4;

            for (int i = 0; i < 16; i++) xResist[i] = yResist[i] = zResist[i] = 0;

            LevelChunkSection subChunk = chunk.getSection(cY);
            checksum = 0;

            if (subChunk != null) {
                for(int iX = 0; iX < 16; iX++) {
                    for(int iY = 0; iY < 16; iY ++) {
                        for(int iZ = 0; iZ < 16; iZ ++) {
                            BlockState state = subChunk.getBlockState(iX, iY, iZ);
                            if (state.isAir()) continue;
                            float resistance = Math.min(state.getExplosionResistance(level, new BlockPos(tX + iX, tY + iY, tZ + iZ), null), 100);
                            xResist[iX] += resistance;
                            yResist[iY] += resistance;
                            zResist[iZ] += resistance;
                            checksum += state.hashCode() & 0xff;
                        }
                    }
                }
            }

            needsRebuild = false;
            return this;
        }

        public float getResistanceValue(Direction direction) {
            return switch (direction) {
                case EAST -> getResistanceFromArray(xResist, true);
                case WEST -> getResistanceFromArray(xResist, false);
                case UP -> getResistanceFromArray(yResist, true);
                case DOWN -> getResistanceFromArray(yResist, false);
                case SOUTH -> getResistanceFromArray(zResist, true);
                case NORTH -> getResistanceFromArray(zResist, false);
            };
        }

        private float getResistanceFromArray(float[] resist, boolean inverse) {
            float res = 0;
            for (int i = 0; i < 16; i++) {
                res += resist[inverse ? 15 - i : i] / 15 * i;
            }
            return res;
        }
    }
}
