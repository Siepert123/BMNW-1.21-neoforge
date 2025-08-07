package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

public class PlagiarizedExplosionHandlerBatched implements Exploder {
    public HashMap<ChunkPos, List<Vector3f>> perChunk = new HashMap<>();
    public List<ChunkPos> orderedChunks = new ArrayList<>();
    private final CoordComparator comparator = new CoordComparator();
    private final int posX;
    private final int posY;
    private final int posZ;
    private final Level level;

    private final int strength;
    private final int length;
    private final int speed;
    private final int gspNumMax;
    private int gspNum;
    private double gspX, gspY;

    public boolean collectionComplete = false;

    private final long startMs;

    public Runnable onFinish = null;
    public PlagiarizedExplosionHandlerBatched setOnFinish(Runnable func) {
        this.onFinish = func;
        return this;
    }

    public PlagiarizedExplosionHandlerBatched(Level level, int x, int y, int z, int strength, int speed, int length) {
        this.level = level;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.strength = strength;
        this.speed = speed;
        this.length = length;

        this.gspNumMax = (int)(2.5 * Math.PI * Math.pow(this.strength, 2));
        this.gspNum = 1;

        this.gspX = Math.PI;
        this.gspY = 0.0;

        this.startMs = System.currentTimeMillis();
    }

    private void generateGspUp() {
        if (this.gspNum < this.gspNumMax) {
            int k = this.gspNum + 1;
            double hk = -1.0 + 2.0 * (k - 1.0) / (this.gspNumMax - 1.0);
            this.gspX = Math.acos(hk);

            double prevLon = this.gspY;
            double lon = prevLon + 3.6 / Math.sqrt(this.gspNumMax) / Math.sqrt(1.0 - hk * hk);
            this.gspY = lon % (Math.PI * 2);
        } else {
            this.gspX = 0.0;
            this.gspY = 0.0;
        }
        this.gspNum++;
    }

    private Vec3 getSpherical2cartesian() {
        double dx = Math.sin(this.gspX) * Math.cos(this.gspY);
        double dz = Math.sin(this.gspX) * Math.sin(this.gspY);
        double dy = Math.cos(this.gspX);
        return new Vec3(dx, dy, dz);
    }

    public void collectTip(int count) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int amountProcessed = 0;

        while(this.gspNumMax >= this.gspNum){
            // Get Cartesian coordinates for spherical coordinates
            Vec3 vec = this.getSpherical2cartesian();

            int length = this.strength;
            float res = this.strength;

            Vector3f lastPos = null;
            HashSet<ChunkPos> chunkCoords = new HashSet<>();

            for(int i = 0; i < length; i ++) {

                if(i > this.length)
                    break;

                float x0 = (float) (posX + (vec.x * i));
                float y0 = (float) (posY + (vec.y * i));
                float z0 = (float) (posZ + (vec.z * i));

                int iX = (int) Math.floor(x0);
                int iY = (int) Math.floor(y0);
                int iZ = (int) Math.floor(z0);

                double fac = 100 - ((double) i) / ((double) length) * 100;
                fac *= 0.07D;

                Block block = this.level.getBlockState(mutable.set(iX, iY, iZ)).getBlock();

                if(!(block instanceof LiquidBlock))
                    res -= (float) Math.pow(getResistance(block), 7.5D - fac);

                if(res > 0 && block != Blocks.AIR) {
                    lastPos = new Vector3f(x0, y0, z0);
                    //all-air chunks don't need to be buffered at all
                    ChunkPos chunkPos = new ChunkPos(iX >> 4, iZ >> 4);
                    chunkCoords.add(chunkPos);
                }

                if(res <= 0 || i + 1 >= this.length || i == length - 1) {
                    break;
                }
            }

            for(ChunkPos pos : chunkCoords) {
                List<Vector3f> triplets = perChunk.computeIfAbsent(pos, k -> new ArrayList<>());

                //we re-use the same pos instead of using individualized per-chunk ones to save on RAM

                triplets.add(lastPos);
            }

            // Raise one generalized spiral points
            this.generateGspUp();

            amountProcessed++;
            if(amountProcessed >= count) {
                return;
            }
        }

        this.orderedChunks.addAll(this.perChunk.keySet());
        this.orderedChunks.sort(this.comparator);

        this.collectionComplete = true;
    }

    @SuppressWarnings("deprecation")
    public static float getResistance(Block block) {
        if (block == Blocks.OBSIDIAN) return Blocks.STONE.getExplosionResistance() * 3;
        return block.getExplosionResistance();
    }

    public class CoordComparator implements Comparator<ChunkPos> {
        @Override
        public int compare(ChunkPos o1, ChunkPos o2) {
            int x = PlagiarizedExplosionHandlerBatched.this.posX >> 4;
            int z = PlagiarizedExplosionHandlerBatched.this.posZ >> 4;

            int d1 = Math.abs((x - o1.x)) + Math.abs((z - o1.z));
            int d2 = Math.abs((x - o2.x)) + Math.abs((z - o2.z));

            return d1 - d2;
        }
    }

    public void processChunk() {
        if (this.perChunk.isEmpty()) return;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        ChunkPos pos = this.orderedChunks.getFirst();
        List<Vector3f> list = this.perChunk.get(pos);
        HashSet<BlockPos> toRem = new HashSet<>();
        HashSet<BlockPos> toRemTips = new HashSet<>();

        int chunkX = pos.x;
        int chunkZ = pos.z;

        int enter = (int) (Math.min(
                Math.abs(this.posX - (chunkX << 4)),
                Math.abs(this.posZ - (chunkZ << 4))
        )) - 16;

        enter = Math.max(enter, 0);

        for (Vector3f vec3f : list) {
            Vec3 vec = new Vec3(vec3f.x - this.posX, vec3f.y - this.posY, vec3f.z - this.posZ);
            double pX = vec.x / vec.length();
            double pY = vec.y / vec.length();
            double pZ = vec.z / vec.length();

            int tipX = (int) Math.floor(vec3f.x);
            int tipY = (int) Math.floor(vec3f.y);
            int tipZ = (int) Math.floor(vec3f.z);

            boolean inChunk = false;
            for (int i = enter; i < vec.length(); i++) {
                int x = Mth.floor(this.posX + pX * i);
                int y = Mth.floor(this.posY + pY * i);
                int z = Mth.floor(this.posZ + pZ * i);

                if (x >> 4 != chunkX || z >> 4 != chunkZ) {
                    if (inChunk) {
                        break;
                    } else {
                        continue;
                    }
                }

                inChunk = true;

                if (!this.level.getBlockState(mutable.set(x, y, z)).isAir()) {
                    BlockPos imm = mutable.immutable();
                    if (x == tipX && y == tipY && z == tipZ) {
                        toRemTips.add(imm);
                    }
                    toRem.add(imm);
                }
            }
        }

        for (BlockPos bp : toRem) {
            if (toRemTips.contains(bp)) {
                this.handleTip(bp);
            } else {
                this.level.setBlock(bp, Blocks.AIR.defaultBlockState(), 2 | 16 | 32);
            }
        }

        this.perChunk.remove(pos);
        this.orderedChunks.removeFirst();
    }

    private void handleTip(BlockPos pos) {
        this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3 | 16 | 32);
    }

    @Override
    public boolean isComplete() {
        if (this.collectionComplete && this.perChunk.isEmpty()) {
            this.onRemoveDebug();
            return true;
        }
        return false;
    }

    @Override
    public void cacheChunksTick(int msBudget) {
        if (!this.collectionComplete) {
            this.collectTip(this.speed * 10);
        }
    }

    @Override
    public void destructionTick(int msBudget) {
        if (!this.collectionComplete) return;
        long start = System.currentTimeMillis();
        while (!this.perChunk.isEmpty() && System.currentTimeMillis() < start + msBudget)
            this.processChunk();
    }

    @Override
    public void cancel() {
        this.collectionComplete = true;
        if (this.perChunk != null) this.perChunk.clear();
        if (this.orderedChunks != null) this.orderedChunks.clear();
    }

    @Override
    public void onRemoveDebug() {
        if (this.onFinish != null) {
            this.onFinish.run();
        }
        long ms = System.currentTimeMillis() - this.startMs;
        System.out.println("Explosion took " + ms + " milliseconds");
    }
}
