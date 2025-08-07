package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.misc.SubChunkKey;
import nl.melonstudios.bmnw.misc.SubChunkSnapshot;
import nl.melonstudios.bmnw.misc.math.ConcurrentBitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Why do effort when you can take inspiration?
 * This code was mostly based off of HBMs code with a few tweaks.
 * <p>
 * (hbm pls don't sue :pleading~2:)
 */
public class PlagiarizedExplosionHandlerParallelized implements Exploder {
    private static final boolean USE_SECOND_THING = false;

    private static final Logger LOGGER = LogManager.getLogger("PlagiarizedExplosionHandler");

    private static final float NUKE_RESISTANCE_CUTOFF = 2_000_000F;
    private static final float INITIAL_ENERGY_FACTOR = 0.3F;
    private static final double RESOLUTION_FACTOR = 1.0;

    public static final ArrayList<PlagiarizedExplosionHandlerParallelized> ALL_HANDLERS = new ArrayList<>();

    protected final Level level;
    private final int worldHeight;
    private final int minWorldHeight;
    private final int maxWorldHeight;
    private final int bitsetSize;
    private final int subchunkPerChunk;
    private final int minSubChunk;

    private final double explosionX, explosionY, explosionZ;
    private final int originX, originY, originZ;
    private final int strength;
    private final int radius;

    private final CompletableFuture<List<Vec3>> directionsFuture;
    private final ConcurrentMap<ChunkPos, ConcurrentBitSet> destructionMap;
    private final ConcurrentMap<ChunkPos, ConcurrentMap<Integer, DoubleAdder>> damageMap;
    private final ConcurrentMap<SubChunkKey, SubChunkSnapshot> snapshots;
    private final ConcurrentMap<SubChunkKey, ConcurrentLinkedQueue<RayTask>> waitingRoom;
    private final BlockingQueue<RayTask> rayQueue;
    private final ExecutorService pool;
    private final CountDownLatch latch;
    private final Thread latchWatcherThread;
    private final List<ChunkPos> orderedChunks;
    private final BlockingQueue<SubChunkKey> highPriorityReactiveQueue;
    private final Iterator<SubChunkKey> lowPriorityProactiveIterator;
    private volatile List<Vec3> directions;
    private volatile boolean collectFinished = false;
    private volatile boolean consolidationFinished = false;
    private volatile boolean destroyFinished = false;

    public PlagiarizedExplosionHandlerParallelized(Level level, double x, double y, double z, int strength, int speed, int radius) {
        this.level = level;

        this.worldHeight = this.level.getMaxBuildHeight() - this.level.getMinBuildHeight();
        this.minWorldHeight = this.level.getMinBuildHeight();
        this.maxWorldHeight = this.level.getMaxBuildHeight();
        this.bitsetSize = 16 * this.worldHeight * 16;
        this.subchunkPerChunk = this.worldHeight >> 4;
        this.minSubChunk = this.level.getMinSection();

        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;

        this.originX = Mth.floor(x);
        this.originY = Mth.floor(y);
        this.originZ = Mth.floor(z);

        this.strength = strength;
        this.radius = radius;

        int rayCount = Math.max(0, (int) (2.5 * Math.PI * strength * strength * RESOLUTION_FACTOR));
        this.latch = new CountDownLatch(rayCount);
        List<SubChunkKey> sortedSubChunks = this.getAllSubChunks();
        this.lowPriorityProactiveIterator = sortedSubChunks.iterator();
        this.highPriorityReactiveQueue = new LinkedBlockingQueue<>();

        int initialChunkCapacity = (int) sortedSubChunks.stream().map(SubChunkKey::getPos).distinct().count();

        this.destructionMap = new ConcurrentHashMap<>(initialChunkCapacity);
        this.damageMap = new ConcurrentHashMap<>(initialChunkCapacity);

        int subChunkCount = sortedSubChunks.size();
        this.snapshots = new ConcurrentHashMap<>(subChunkCount);
        this.waitingRoom = new ConcurrentHashMap<>(subChunkCount);
        this.orderedChunks = new ArrayList<>();

        List<RayTask> initialRayTasks = new ArrayList<>(rayCount);
        for (int i = 0; i < rayCount; i++) initialRayTasks.add(new RayTask(i));
        this.rayQueue = new LinkedBlockingQueue<>(initialRayTasks);

        int workers = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        this.pool = Executors.newWorkStealingPool(workers);
        this.directionsFuture = CompletableFuture.supplyAsync(() -> this.generateSphereRays(rayCount));

        for (int i = 0; i < workers; i++) pool.submit(new Worker());

        this.latchWatcherThread = new Thread(() -> {
            try {
                this.latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("collect finished");
                this.collectFinished = true;
                if (USE_SECOND_THING) this.pool.submit(this::runConsolidation);
                else this.consolidationFinished = true;
            }
        }, "Plagiarized-LatchWatcher-" + Long.toHexString(System.nanoTime()));
        this.latchWatcherThread.setDaemon(true);
        this.latchWatcherThread.start();
    }

    @SuppressWarnings("deprecation")
    private static float getResistance(Block block) {
        if (block instanceof LiquidBlock) return 0.1F;
        if (block == Blocks.OBSIDIAN) return Blocks.STONE.getExplosionResistance() * 3.0F;
        return block.getExplosionResistance();
    }

    private List<SubChunkKey> getAllSubChunks() {
        List<SubChunkKey> keys = new ArrayList<>();
        int cr = (this.radius + 15) >> 4;
        int minCX = (this.originX >> 4) - cr;
        int maxCX = (this.originX >> 4) + cr;
        int minCZ = (this.originZ >> 4) - cr;
        int maxCZ = (this.originZ >> 4) + cr;
        int minSubY = Math.max(this.minSubChunk, (this.originY - this.radius) >> 4);
        int maxSubY = Math.max(this.minSubChunk + this.subchunkPerChunk - 1, (this.originY + this.radius) >> 4);
        int originSubY = this.originY >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                for (int subY = minSubY; subY <= maxSubY; subY++) {
                    int ccx = (cx << 4) + 8;
                    int ccy = (subY << 4) + 8;
                    int ccz = (cz << 4) + 8;
                    double dx = ccx - this.explosionX;
                    double dy = ccy - this.explosionY;
                    double dz = ccz - this.explosionZ;
                    if (dx * dx + dy * dy + dz * dz <= (this.radius + 14) * (this.radius + 14)) {
                        keys.add(new SubChunkKey(cx, cz, subY));
                    }
                }
            }
        }

        keys.sort(Comparator.comparingInt(key -> {
            ChunkPos pos = key.getPos();
            int distCX = pos.x - (this.originX >> 4);
            int distCZ = pos.z - (this.originZ >> 4);
            int distSubY = key.getSubY() - originSubY;
            return distCX * distCX + distCZ * distCZ + distSubY * distSubY;
        }));

        return keys;
    }

    public void cacheChunksTick(int msBudget) {
        if (this.collectFinished) return;
        final long deadline = System.nanoTime() + (msBudget * 1_000_000L);
        while (System.nanoTime() < deadline) {
            SubChunkKey key = this.highPriorityReactiveQueue.poll();
            if (key == null) break;
            this.processCacheKey(key);
        }
        while (System.nanoTime() < deadline && this.lowPriorityProactiveIterator.hasNext()) {
            SubChunkKey key = this.lowPriorityProactiveIterator.next();
            this.processCacheKey(key);
        }
    }

    private void processCacheKey(SubChunkKey key) {
        if (this.snapshots.containsKey(key)) return;
        this.snapshots.put(key, SubChunkSnapshot.getSnapshot(this.level, key, true));
        ConcurrentLinkedQueue<RayTask> waiters = this.waitingRoom.remove(key);
        if (waiters != null) this.rayQueue.addAll(waiters);
    }

    public void destructionTick(int msBudget) {
        if (!this.collectFinished || !this.consolidationFinished || this.destroyFinished) return;

        final long deadline = System.nanoTime() + (msBudget * 1_000_000L);

        if (this.orderedChunks.isEmpty() && !this.destructionMap.isEmpty()) {
            this.orderedChunks.addAll(this.destructionMap.keySet());
            this.orderedChunks.sort(Comparator.comparingInt(c -> Math.abs((this.originX >> 4) - c.x) + Math.abs((this.originZ >> 4) - c.z)));
        }

        Iterator<ChunkPos> it = this.orderedChunks.iterator();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        while (it.hasNext() && System.nanoTime() < deadline) {
            ChunkPos cp = it.next();
            ConcurrentBitSet bitSet = this.destructionMap.get(cp);
            if (bitSet == null) {
                it.remove();
                continue;
            }

            LevelChunk chunk = this.level.getChunk(cp.x, cp.z);
            LevelChunkSection[] sections = chunk.getSections();
            boolean modified = false;

            for (int subY = 0; subY < sections.length; subY++) {
                LevelChunkSection section = sections[subY];
                if (section.hasOnlyAir()) continue;

                int startBit = (this.worldHeight - 1 - ((subY << 4) + 15)) << 8;
                int endBit = ((this.worldHeight - 1 - (subY << 4)) << 8) | 0xFF;

                int bit = bitSet.nextSetBit(startBit);

                while (bit >= 0 && bit <= endBit && System.nanoTime() < deadline) {
                    int yGlobal = this.worldHeight - 1 - (bit >>> 8) + this.minWorldHeight;
                    int xGlobal = (cp.x << 4) | ((bit >>> 4) & 0xF);
                    int zGlobal = (cp.z << 4) | (bit & 0xF);
                    int xLocal = xGlobal & 0xF;
                    int yLocal = yGlobal & 0xF;
                    int zLocal = zGlobal & 0xF;
                    if (section.getBlockState(xLocal, yLocal, zLocal).getBlock() != Blocks.AIR) {
                        if (this.level.getBlockEntity(mutable.set(xGlobal, yGlobal, zGlobal)) != null) {
                            this.level.removeBlockEntity(mutable);
                        }

                        section.setBlockState(xLocal, yLocal, zLocal, Blocks.AIR.defaultBlockState(), false);
                        modified = true;

                        this.level.blockUpdated(mutable, Blocks.AIR);

                        this.level.getLightEngine().checkBlock(mutable);
                    }
                    bitSet.clear(bit);
                    bit = bitSet.nextSetBit(bit+1);
                }
            }

            if (modified) {
                chunk.setUnsaved(true);
            }
            if (bitSet.isEmpty()) {
                this.destructionMap.remove(cp);
                for (int subY = 0; subY < this.subchunkPerChunk; subY++) this.snapshots.remove(new SubChunkKey(cp, subY + this.minSubChunk));
                it.remove();
            }
        }

        if (this.orderedChunks.isEmpty() && this.destructionMap.isEmpty()) {
            System.out.println("destroy finished");
            this.destroyFinished = true;
            if (this.pool != null) this.pool.shutdown();
        }
    }

    public boolean isComplete() {
        return this.collectFinished && this.consolidationFinished && this.destroyFinished;
    }

    public void cancel() {
        this.collectFinished = true;
        this.consolidationFinished = true;
        this.destroyFinished = true;

        if (this.rayQueue != null) this.rayQueue.clear();
        if (this.waitingRoom != null) this.waitingRoom.clear();

        if (this.latch != null) while (this.latch.getCount() > 0) this.latch.countDown();
        if (this.latchWatcherThread != null && this.latchWatcherThread.isAlive()) this.latchWatcherThread.interrupt();

        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdownNow();
            try {
                if (!this.pool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    LOGGER.error("PlagiarizedExplosionHandler did not comply with the cancellation on Twitter.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!this.pool.isShutdown()) this.pool.shutdownNow();
            }
        }

        if (this.destructionMap != null) this.destructionMap.clear();
        if (this.damageMap != null) this.damageMap.clear();
        if (this.snapshots != null) this.snapshots.clear();
        if (this.orderedChunks != null) this.orderedChunks.clear();
    }

    private List<Vec3> generateSphereRays(int count) {
        List<Vec3> list = new ArrayList<>(count);
        if (count == 0) return list;
        if (count == 1) {
            list.add(new Vec3(1, 0, 0));
            return list;
        }
        double phi = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int i = 0; i < count; i++) {
            double y = 1.0 - (i / (double) (count - 1)) * 2.0;
            double r = Math.sqrt(1.0 - y * y);
            double t = phi * i;
            list.add(new Vec3(Math.cos(t) * r, y, Math.sin(t) * r));
        }
        return list;
    }

    private void runConsolidation() {
        this.damageMap.forEach((cp, innerDamageMap) -> {
            if (innerDamageMap.isEmpty()) {
                this.damageMap.remove(cp);
                return;
            }

            ConcurrentBitSet bitSet = this.destructionMap.computeIfAbsent(cp, k -> new ConcurrentBitSet(this.bitsetSize));
            innerDamageMap.forEach((bitIndex, accumulatedDamageAdder) -> {
                float accumulatedDamage = (float) accumulatedDamageAdder.sum();
                if (accumulatedDamage <= 0.0F) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                int yGlobal = this.worldHeight - 1 - (bitIndex >>> 8);
                int subY = yGlobal >> 4;
                if (subY < 0) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }
                SubChunkKey key = new SubChunkKey(cp, this.minSubChunk + subY);
                SubChunkSnapshot snapshot = this.snapshots.get(key);
                if (snapshot == null || snapshot == SubChunkSnapshot.EMPTY) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                int xLocal = (bitIndex >>> 4) & 0xF;
                int zLocal = bitIndex & 0xF;
                Block originalBlock = snapshot.getBlock(xLocal, yGlobal & 0xF, zLocal);
                if (originalBlock == Blocks.AIR) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                float resistance = getResistance(originalBlock);
                if (accumulatedDamage >= resistance * RESOLUTION_FACTOR) bitSet.set(bitIndex);
                innerDamageMap.remove(bitIndex);
            });
            if (innerDamageMap.isEmpty()) this.damageMap.remove(cp);
        });
        this.damageMap.clear();
        System.out.println("finished 'consolidation' (whatever that is)");
        this.consolidationFinished = true;
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!collectFinished && !Thread.currentThread().isInterrupted()) {
                    RayTask task = rayQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) task.trace();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class RayTask {
        private static final double RAY_DIRECTION_EPSILON = 1e-6;
        private static final double PROCESSING_EPSILON = 1e-9;
        private static final float MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC = 0.01F;

        private final int dirIndex;
        private int x, y, z;
        private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        private float energy;
        private double tMaxX, tMaxY, tMaxZ, tDeltaX, tDeltaY, tDeltaZ;
        private int stepX, stepY, stepZ;
        private boolean initialized = false;
        private double currentRayPosition;

        private int lastCX = Integer.MIN_VALUE, lastCZ = Integer.MIN_VALUE, lastSubY = Integer.MIN_VALUE;
        private SubChunkKey currentSBKey = null;

        private RayTask(int dirIndex) {
            this.dirIndex = dirIndex;
        }

        private void init() {
            if (directions == null) directions = directionsFuture.join();
            Vec3 dir = directions.get(this.dirIndex);
            this.energy = strength * INITIAL_ENERGY_FACTOR;
            double px = explosionX;
            double py = explosionY;
            double pz = explosionZ;
            this.x = originX;
            this.y = originY;
            this.z = originZ;
            this.currentRayPosition = 0.0;

            double dirX = dir.x;
            double dirY = dir.y;
            double dirZ = dir.z;

            double absDirX = Math.abs(dirX);
            this.stepX = (absDirX < RAY_DIRECTION_EPSILON) ? 0 : (dirX > 0 ? 1 : -1);
            this.tDeltaX = (this.stepX == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirX;
            this.tMaxX = (this.stepX == 0) ? Double.POSITIVE_INFINITY : ((this.stepX > 0 ? (this.x + 1 - px) : (px - this.x)) * this.tDeltaX);

            double absDirY = Math.abs(dirY);
            this.stepY = (absDirY < RAY_DIRECTION_EPSILON) ? 0 : (dirY > 0 ? 1 : -1);
            this.tDeltaY = (this.stepY == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirX;
            this.tMaxY = (this.stepY == 0) ? Double.POSITIVE_INFINITY : ((this.stepY > 0 ? (this.y + 1 - py) : (py - this.y)) * this.tDeltaY);

            double absDirZ = Math.abs(dirZ);
            this.stepZ = (absDirZ < RAY_DIRECTION_EPSILON) ? 0 : (dirZ > 0 ? 1 : -1);
            this.tDeltaZ = (this.stepZ == 0) ? Double.POSITIVE_INFINITY : 1.0 / absDirX;
            this.tMaxZ = (this.stepZ == 0) ? Double.POSITIVE_INFINITY : ((this.stepZ > 0 ? (this.z + 1 - pz) : (pz - this.z)) * this.tDeltaZ);

            this.initialized = true;
        }

        private void trace() {
            if (!this.initialized) this.init();
            if (this.energy <= 0) {
                latch.countDown();
                return;
            }

            while (this.energy > 0) {
                if (this.y < minWorldHeight || y >= maxWorldHeight || Thread.currentThread().isInterrupted()) break;
                if (this.currentRayPosition >= radius - PROCESSING_EPSILON) break;

                int cx = this.x >> 4;
                int cz = this.z >> 4;
                int subY = this.y >> 4;
                if (cx != this.lastCX || cz != this.lastCZ || subY != this.lastSubY) {
                    this.currentSBKey = new SubChunkKey(cx, cz, subY);
                    this.lastCX = cx;
                    this.lastCZ = cz;
                    this.lastSubY = subY;
                }

                SubChunkSnapshot snapshot = snapshots.get(this.currentSBKey);
                if (snapshot == null) {
                    final boolean[] amFirst = {false};
                    ConcurrentLinkedQueue<RayTask> waiters = waitingRoom.computeIfAbsent(this.currentSBKey, k -> {
                        amFirst[0] = true;
                        return new ConcurrentLinkedQueue<>();
                    });
                    if (amFirst[0]) highPriorityReactiveQueue.add(this.currentSBKey);
                    waiters.add(this);
                    return;
                }

                double tExitVoxel = Math.min(this.tMaxX, Math.min(this.tMaxY, this.tMaxZ));
                double segmentLenInVoxel = tExitVoxel - this.currentRayPosition;
                double segmentLenForProcessing;
                boolean stopAfterThisSegment = false;

                if (this.currentRayPosition + segmentLenInVoxel > radius - PROCESSING_EPSILON) {
                    segmentLenForProcessing = Math.max(0.0, radius - this.currentRayPosition);
                    stopAfterThisSegment = true;
                } else segmentLenForProcessing = segmentLenInVoxel;

                if (snapshot != SubChunkSnapshot.EMPTY && segmentLenForProcessing > PROCESSING_EPSILON) {
                    Block block = snapshot.getBlock(this.x & 0xF, this.y & 0xF, this.z & 0xF);
                    if (block != Blocks.AIR) {
                        float resistance = getResistance(block);
                        if (resistance >= NUKE_RESISTANCE_CUTOFF) {
                            this.energy = 0.0F;
                        } else {
                            double energyLossFactor = this.getEnergyLossFactor(resistance);
                            float damageDealt = (float) (energyLossFactor * segmentLenForProcessing);
                            this.energy -= damageDealt;
                            if (damageDealt > 0) {
                                int bitIndex = ((worldHeight - 1 - (this.y - minWorldHeight)) << 8) | ((this.x & 0xF) << 4) | (this.z & 0xF);
                                ChunkPos pos = this.currentSBKey.getPos();
                                if (USE_SECOND_THING) {
                                    damageMap.computeIfAbsent(pos, cp -> new ConcurrentHashMap<>(256))
                                            .computeIfAbsent(bitIndex, k -> new DoubleAdder()).add(damageDealt);
                                } else if (this.energy > 0) {
                                    destructionMap.computeIfAbsent(pos, k -> new ConcurrentBitSet(bitsetSize)).set(bitIndex);
                                }
                            }
                        }
                    }
                }

                this.currentRayPosition = tExitVoxel;
                if (this.energy <= 0 || stopAfterThisSegment) break;

                if (this.tMaxX < this.tMaxY) {
                    if (this.tMaxX < this.tMaxZ) {
                        this.x += this.stepX;
                        this.tMaxX += this.tDeltaX;
                    } else {
                        this.z += this.stepZ;
                        this.tMaxZ += this.tDeltaZ;
                    }
                } else {
                    if (this.tMaxY < this.tMaxZ) {
                        this.y += this.stepY;
                        this.tMaxY += this.tDeltaY;
                    } else {
                        this.z += this.stepZ;
                        this.tMaxZ += this.tDeltaZ;
                    }
                }
            }
            latch.countDown();
        }

        private double getEnergyLossFactor(float resistance) {
            double effectiveDist = Math.max(this.currentRayPosition, MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC);
            return (Math.pow(resistance + 1.0, 3.0 * (effectiveDist / radius)) - 1.0);
        }
    }
}