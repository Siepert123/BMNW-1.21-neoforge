package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public class PlagiarizedExplosionHandlerBatched implements Exploder {
    public HashMap<ChunkPos, List<Vector3f>> perChunk = new HashMap<>();
    public List<ChunkPos> orderedChunks = new ArrayList<>();
    private final CoordComparator comparator = new CoordComparator();
    private final int posX;
    private final int posY;
    private final int posZ;
    public ServerLevel level;

    private final float strength;
    private final int length;
    private final int speed;
    private final int gspNumMax;
    private int gspNum;
    private double gspX, gspY;

    public boolean collectionComplete = false;

    private final UUID parentUUID;

    public void writeNBT(CompoundTag nbt) {
        if (!this.perChunk.isEmpty()) {
            System.out.println("writing " + this.perChunk.size() + " per-chunks");
            ListTag list = new ListTag(this.perChunk.size());

            for (Map.Entry<ChunkPos, List<Vector3f>> entry : this.perChunk.entrySet()) {
                CompoundTag tag = new CompoundTag();

                tag.putInt("cX", entry.getKey().x);
                tag.putInt("cZ", entry.getKey().z);

                ListTag floats = new ListTag(entry.getValue().size());
                for (Vector3f vec : entry.getValue()) {
                    floats.add(new IntArrayTag(
                            new int[]{
                                    Float.floatToRawIntBits(vec.x),
                                    Float.floatToRawIntBits(vec.y),
                                    Float.floatToRawIntBits(vec.z),
                            }
                    ));
                }
                tag.put("Vectors", floats);

                list.add(tag);
            }

            nbt.put("PerChunk", list);
        }

        if (!this.orderedChunks.isEmpty()) {
            long[] packed = new long[this.orderedChunks.size()];
            for (int i = 0; i < this.orderedChunks.size(); i++) {
                packed[i] = this.orderedChunks.get(i).toLong();
            }
            nbt.putLongArray("OrderedChunks", packed);
        }

        nbt.putInt("posX", this.posX);
        nbt.putInt("posY", this.posY);
        nbt.putInt("posZ", this.posZ);

        nbt.putFloat("strength", this.strength);
        nbt.putInt("length", this.length);
        nbt.putInt("gspNumMax", this.gspNumMax);
        nbt.putInt("gspNum", this.gspNum);
        nbt.putDouble("gspX", this.gspX);
        nbt.putDouble("gspY", this.gspY);

        nbt.putBoolean("collectionComplete", this.collectionComplete);

        if (this.parentUUID != null) nbt.putUUID("ParentUUID", this.parentUUID);
    }

    public PlagiarizedExplosionHandlerBatched(CompoundTag nbt) {
        if (nbt.contains("PerChunk", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("PerChunk", Tag.TAG_COMPOUND);

            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);

                ChunkPos pos = new ChunkPos(tag.getInt("cX"), tag.getInt("cZ"));
                ListTag floats = tag.getList("Vectors", Tag.TAG_INT_ARRAY);

                List<Vector3f> vector3fs = new ArrayList<>(floats.size());
                for (int j = 0; j < floats.size(); j++) {
                    int[] array = floats.getIntArray(j);
                    vector3fs.add(new Vector3f(
                            Float.intBitsToFloat(array[0]),
                            Float.intBitsToFloat(array[1]),
                            Float.intBitsToFloat(array[2])
                    ));
                }

                this.perChunk.put(pos, vector3fs);
            }
        }

        if (nbt.contains("OrderedChunks", Tag.TAG_LONG_ARRAY)) {
            long[] packed = nbt.getLongArray("OrderedChunks");
            for (long l : packed) {
                this.orderedChunks.add(new ChunkPos(l));
            }
        }

        this.posX = nbt.getInt("posX");
        this.posY = nbt.getInt("posY");
        this.posZ = nbt.getInt("posZ");

        this.strength = nbt.getFloat("strength");
        this.speed = BMNWServerConfig.explosionCalculationFactor();
        this.length = nbt.getInt("length");
        this.gspNumMax = nbt.getInt("gspNumMax");
        this.gspNum = nbt.getInt("gspNum");
        this.gspX = nbt.getDouble("gspX");
        this.gspY = nbt.getDouble("gspY");

        this.collectionComplete = nbt.getBoolean("collectionComplete");

        if (nbt.contains("ParentUUID")) this.parentUUID = nbt.getUUID("ParentUUID");
        else this.parentUUID = null;
    }

    public PlagiarizedExplosionHandlerBatched(ServerLevel level, int x, int y, int z, float strength, int speed, int length, @Nullable UUID parentUUID) {
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

        this.parentUUID = parentUUID;

        LevelActiveExplosions.get(this.level).setDirty();
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

        LevelActiveExplosions.get(this.level).setDirty();

        while(this.gspNumMax >= this.gspNum){
            // Get Cartesian coordinates for spherical coordinates
            Vec3 vec = this.getSpherical2cartesian();

            float length = this.strength;
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

        LevelActiveExplosions.get(this.level).setDirty();

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
        if (this.parentUUID != null) {
            if (this.level instanceof ServerLevel level) {
                LevelEntityGetter<Entity> entityLevelEntityGetter = level.getEntities();
                Entity candidate = entityLevelEntityGetter.get(this.parentUUID);
                if (candidate != null) {
                    ((ExploderParent)candidate).notifyExplosionFinished();
                }
            }
        }

        LevelActiveExplosions.get(this.level).setDirty();
    }
}
