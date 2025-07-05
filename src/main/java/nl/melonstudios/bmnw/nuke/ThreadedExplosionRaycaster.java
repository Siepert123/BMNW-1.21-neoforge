package nl.melonstudios.bmnw.nuke;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.nuke.math.Raycast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreadedExplosionRaycaster {
    private static final Logger LOGGER = LogManager.getLogger();
    private static int nextThreadID = 0;
    private final ServerLevel level;
    private final float initialStrength;
    private final int expectedRange;
    private final Vector3fc source;
    public final HashMap<ChunkPos, boolean[]> dataMap = new HashMap<>();

    private boolean isDoneRaycasting = false;

    public ThreadedExplosionRaycaster(ServerLevel level, Position source, float initialStrength) {
        this(level, new Vector3f((float) source.x(), (float) source.y(), (float) source.z()), initialStrength);
    }
    public ThreadedExplosionRaycaster(ServerLevel level, Vector3fc source, float initialStrength) {
        this.level = level;
        this.initialStrength = initialStrength;
        this.expectedRange = Mth.ceil(initialStrength * 2) + 5;
        this.source = source;
    }

    public boolean isDoneRaycasting() {
        return this.isDoneRaycasting;
    }

    private float determineResolution() {
        float circ = Mth.TWO_PI * this.expectedRange;
        return 360F / circ;
    }

    public void startRaycasting() {
        this.isDoneRaycasting = false;
        try {
            float resolution = this.determineResolution();
            RandomSource rnd = RandomSource.create();
            Raycast raycast = new Raycast(this.source);
            float dx, dy, dz, temp, ryaw, rpitch, str;
            int cx, cz, minSection, idx;
            ChunkPos chunkPos;
            ChunkPos curChunkPos = null;
            boolean[] curArray = null;
            BlockPos.MutableBlockPos pos = raycast.getCurrentBlockPos();
            minSection = this.level.getMinSection();
            for (float yaw = 0; yaw <= 360; yaw += resolution) {
                for (float pitch = 90; pitch >= -90; pitch -= resolution) {
                    LOGGER.debug("Ray Y:{} P:{}", yaw, pitch);
                    ryaw = Mth.DEG_TO_RAD * yaw;
                    rpitch = Mth.DEG_TO_RAD * pitch;
                    dx = Mth.cos(ryaw);
                    dz = Mth.sin(ryaw);
                    dy = Mth.sin(rpitch);
                    temp = Mth.cos(rpitch);
                    dx *= temp;
                    dz *= temp;
                    raycast.reset(dx, dy, dz);
                    str = this.initialStrength * (0.99F + rnd.nextFloat() * 0.02F);
                    while (str > 0.0F) {
                        raycast.step();
                        raycast.getCurrentBlockPos();
                        str -= getBlastRes(this.level.getBlockState(pos)) * getStrMultiplier(this.level, pos.getY());
                        if (str > 0.0F) {
                            cx = SectionPos.blockToSectionCoord(pos.getX());
                            cz = SectionPos.blockToSectionCoord(pos.getZ());
                            chunkPos = this.getChunkpos(cx, cz);
                            if (!this.dataMap.containsKey(chunkPos)) {
                                ChunkAccess chunk = this.level.getChunk(cx, cz);
                                curChunkPos = chunkPos;
                                curArray = new boolean[chunk.getSectionsCount() * 4096];
                                this.dataMap.put(chunkPos, curArray);
                            }
                            if (chunkPos != curChunkPos) {
                                curChunkPos = chunkPos;
                                curArray = this.dataMap.get(chunkPos);
                            }
                            idx = NukeUtils.getSectionIdx(minSection, pos);
                            curArray[idx] = true;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Raycasting failure");
            e.printStackTrace(System.err);
        }
        this.isDoneRaycasting = true;
    }

    private final ArrayList<ChunkPos> createdChunkPositions = new ArrayList<>();
    private ChunkPos getChunkpos(int x, int z) {
        for (ChunkPos pos : this.createdChunkPositions) {
            if (pos.x == x && pos.z == z) return pos;
        }
        ChunkPos pos = new ChunkPos(x, z);
        this.createdChunkPositions.add(pos);
        return pos;
    }

    public void startThreadedRaycasting() {
        new Thread(this::startRaycasting, "Raycasting-" + nextThreadID++).start();
    }

    private static float getBlastRes(BlockState state) {
        return Math.max(state.is(Blocks.OBSIDIAN) ? 6.0F : state.getBlock().getExplosionResistance(), 0.5F);
    }

    private static float getStrMultiplier(ServerLevel level, int y) {
        int sea = level.getSeaLevel();
        if (y > sea) return 1.0F;
        int minY = level.getMinBuildHeight();
        int space = sea - minY;
        float adjusted = y - minY;
        return Mth.lerp(1.0F - (adjusted / space), 1.0F, 5.0F);
    }

    public Iterable<Map.Entry<ChunkPos, boolean[]>> getDataIterable() {
        return Library.wrapIterator(this.dataMap.entrySet().iterator());
    }
}
