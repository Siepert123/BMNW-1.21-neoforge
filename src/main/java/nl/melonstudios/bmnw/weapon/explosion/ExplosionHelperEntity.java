package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.init.*;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.weapon.RadiationLingerEntity;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;
import nl.melonstudios.bmnw.wifi.PacketMushroomCloud;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ExplosionHelperEntity extends Entity implements ExploderParent, IEntityWithComplexSpawn {
    private static final Logger LOGGER = LogManager.getLogger("ExplosionHelperEntity");

    public ExplosionHelperEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);

        this.mutable1 = new BlockPos.MutableBlockPos();
        this.plant = new HashSet<>();
        for (Holder<Block> b : BuiltInRegistries.BLOCK.getTagOrEmpty(BMNWTags.Blocks.PLANT)) {
            plant.add(b.value());
        }
        this.logs = new HashSet<>();
        for (Holder<Block> b : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.LOGS_THAT_BURN)) {
            logs.add(b.value());
        }
        this.planks = new HashSet<>();
        for (Holder<Block> b : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.PLANKS)) {
            if (b.value() != Blocks.CRIMSON_PLANKS && b.value() != Blocks.WARPED_PLANKS) planks.add(b.value());
        }
        this.coals = new HashSet<>();
        for (Holder<Block> b : BuiltInRegistries.BLOCK.getTagOrEmpty(Tags.Blocks.ORES_COAL)) {
            coals.add(b.value());
        }
    }

    public ExplosionHelperEntity(Level level, Vec3 pos, NukeType type) {
        this(BMNWEntityTypes.EXPLOSION_HELPER.get(), level);
        this.setPos(pos);
        this.nukeType = Objects.requireNonNull(type);
        this.evaporationMin = Math.max(this.getBlockY() - this.nukeType.getBlastRadius(), level.getMinBuildHeight());
        this.evaporationMax = Math.min(this.getBlockY() + this.nukeType.getBlastRadius()+1, level.getMaxBuildHeight());

        int r = this.nukeType.getEntityBlowRadius();
        this.entitySearch = new AABB(this.position().add(-r, -r, -r), this.position().add(r, r, r));

        this.waterRefilled = BMNWServerConfig.experimentalWaterRefill();

        LOGGER.debug("New explosion created with ID {}", this.getId());
        this.start = System.currentTimeMillis();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putString("nukeType", Objects.requireNonNull(BMNWResourceKeys.NUKE_TYPE_REGISTRY.getKey(this.nukeType)).toString());

        nbt.putInt("mutable1X", this.mutable1.getX());
        nbt.putInt("mutable1Y", this.mutable1.getY());
        nbt.putInt("mutable1Z", this.mutable1.getZ());

        nbt.putLong("start", System.currentTimeMillis() - this.start);

        nbt.putInt("affectedMinCX", this.affectedMinCX);
        nbt.putInt("affectedMaxCX", this.affectedMaxCX);
        nbt.putInt("affectedMinCZ", this.affectedMinCZ);
        nbt.putInt("affectedMaxCZ", this.affectedMaxCZ);

        nbt.putInt("evaporationMin", this.evaporationMin);
        nbt.putInt("evaporationMax", this.evaporationMax);

        if (this.orderedChunks != null) {
            long[] orderedChunksArray = new long[this.orderedChunks.size()];
            for (int i = 0; i < this.orderedChunks.size(); i++) {
                orderedChunksArray[i] = this.orderedChunks.get(i).toLong();
            }
            nbt.putLongArray("OrderedChunks", orderedChunksArray);
        }

        nbt.putBoolean("hasSpawnedCloud", this.hasSpawnedCloud);
        nbt.putBoolean("waterEvaporated", this.waterEvaporated);
        nbt.putBoolean("explosionStarted", this.explosionStarted);
        nbt.putBoolean("explosionFinished", this.explosionFinished);
        nbt.putBoolean("leavesDestroyed", this.leavesDestroyed);
        nbt.putBoolean("treesCharred", this.treesCharred);
        nbt.putBoolean("radiationReleased", this.radiationReleased);
        nbt.putBoolean("nuclearRemainsPlaced", this.nuclearRemainsPlaced);
        nbt.putBoolean("waterRefilled", this.waterRefilled);

        nbt.putInt("age", this.age);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.nukeType = BMNWResourceKeys.NUKE_TYPE_REGISTRY.get(ResourceLocation.parse(nbt.getString("nukeType")));

        this.mutable1.set(
                nbt.getInt("mutable1X"),
                nbt.getInt("mutable1Y"),
                nbt.getInt("mutable1Z")
        );

        this.start = System.currentTimeMillis() - nbt.getLong("start");

        this.affectedMinCX = nbt.getInt("affectMinCX");
        this.affectedMaxCX = nbt.getInt("affectMaxCX");
        this.affectedMinCZ = nbt.getInt("affectMinCZ");
        this.affectedMaxCZ = nbt.getInt("affectMaxCZ");

        this.evaporationMin = nbt.getInt("evaporationMin");
        this.evaporationMax = nbt.getInt("evaporationMax");

        if (nbt.contains("OrderedChunks", Tag.TAG_LONG_ARRAY)) {
            this.orderedChunks = new ArrayList<>();
            long[] orderedChunksArray = nbt.getLongArray("OrderedChunks");
            for (long packed : orderedChunksArray) this.orderedChunks.add(new ChunkPos(packed));
        }

        this.hasSpawnedCloud = nbt.getBoolean("hasSpawnedCloud");
        this.waterEvaporated = nbt.getBoolean("waterEvaporated");
        this.explosionStarted = nbt.getBoolean("explosionStarted");
        this.explosionFinished = nbt.getBoolean("explosionFinished");
        this.leavesDestroyed = nbt.getBoolean("leavesDestroyed");
        this.treesCharred = nbt.getBoolean("treesCharred");
        this.radiationReleased = nbt.getBoolean("radiationReleased");
        this.nuclearRemainsPlaced = nbt.getBoolean("nuclearRemainsPlaced");
        this.waterRefilled = nbt.getBoolean("waterRefilled");

        this.age = nbt.getInt("age");

        int r = this.nukeType.getEntityBlowRadius();
        this.entitySearch = new AABB(this.position().add(-r, -r, -r), this.position().add(r, r, r));
    }

    public NukeType nukeType;

    private final BlockPos.MutableBlockPos mutable1;

    private long start;

    private int affectedMinCX, affectedMinCZ;
    private int affectedMaxCX, affectedMaxCZ;

    private int evaporationMin, evaporationMax;

    private List<ChunkPos> orderedChunks = null;

    private boolean hasSpawnedCloud = false;
    private boolean waterEvaporated = false;
    private boolean explosionStarted = false;
    private boolean explosionFinished = false;
    private boolean leavesDestroyed = false;
    private boolean treesCharred = false;
    private boolean radiationReleased = false;
    private boolean nuclearRemainsPlaced = false;
    private boolean waterRefilled = false;

    private final HashSet<Block> plant, logs, planks, coals;
    private AABB entitySearch;

    public int age;

    @Override
    public void tick() {
        this.age++;
        if (this.nukeType == null) return;
        if (this.level().isClientSide) {
            if (this.nukeType.brightensSky()) {
                if (this.age <= this.nukeType.getMushroomCloudSize() * 25.0F) {
                    this.level().setSkyFlashTime(20);
                }
            }
            return;
        }

        if (this.level() instanceof ServerLevel level) {
            if (this.age >= 10) {
                this.age = 0;
                int r = this.nukeType.getEntityBlowRadius();
                int r2 = r * r;
                List<Entity> entities = level.getEntities(this, this.entitySearch);
                for (Entity entity : entities) {
                    double d2 = entity.distanceToSqr(this.position());
                    if (d2 > r2) continue;
                    double d = Math.sqrt(d2);
                    double inv = r - d;
                    Vec3 diff = entity.position().subtract(this.position());
                    Vec3 velocity = diff.normalize().scale(inv * 0.01);

                    boolean flag = entity.getY() >= level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, entity.getBlockX(), entity.getBlockZ())
                            || level.clip(
                            new ClipContext(
                                    entity.position(), this.position(),
                                    ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER,
                                    this
                            )
                    ).getType() == HitResult.Type.MISS;

                    if (flag) {
                        entity.addDeltaMovement(velocity);
                        entity.setRemainingFireTicks(Mth.ceil(inv * this.nukeType.getEntityFireTicks()));
                        entity.hurt(BMNWDamageSources.nuclear_blast(level), (float) inv * this.nukeType.entityDamageMultiplier());
                    }
                }
            }

            if (!this.hasSpawnedCloud) {
                LOGGER.debug("[{}] starting cloud", this.getId());
                PacketDistributor.sendToPlayersInDimension((ServerLevel) level,
                        new PacketMushroomCloud(
                                this.nukeType.isSoulType(),
                                this.getX(), this.getY(), this.getZ(),
                                this.nukeType.getMushroomCloudSize()
                        )
                );
                this.hasSpawnedCloud = true;
                return;
            }
            if (!this.waterEvaporated) {
                if (this.orderedChunks == null) {
                    LOGGER.debug("[{}] starting evaporation", this.getId());
                    int c = (this.nukeType.getBlastRadius() + 15) >> 4;
                    this.affectedMinCX = this.chunkPosition().x - c;
                    this.affectedMaxCX = this.chunkPosition().x + c;
                    this.affectedMinCZ = this.chunkPosition().z - c;
                    this.affectedMaxCZ = this.chunkPosition().z + c;
                    this.orderedChunks = new ArrayList<>();
                    for (int x = this.affectedMinCX; x <= this.affectedMaxCX; x++) {
                        for (int z = this.affectedMinCZ; z <= this.affectedMaxCZ; z++) {
                            this.orderedChunks.add(new ChunkPos(x, z));
                        }
                    }

                    this.orderedChunks.sort((cp1, cp2) -> {
                        int d1 = this.chunkPosition().getChessboardDistance(cp1);
                        int d2 = this.chunkPosition().getChessboardDistance(cp2);

                        return d1 - d2;
                    });
                    return;
                }
                if (this.orderedChunks.isEmpty()) {
                    this.orderedChunks = null;
                    this.waterEvaporated = true;
                } else {
                    int max = Math.max(BMNWServerConfig.explosionCalculationFactor() / 50, 1);
                    for (int i = 0; (i < max && !this.orderedChunks.isEmpty()); i++) {
                        ChunkPos pos = this.orderedChunks.removeFirst();
                        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                        int y;
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (y = this.evaporationMin; y < this.evaporationMax; y++) {
                                    mutable.set(pos.getBlockX(x), y, pos.getBlockZ(z));
                                    if (level.getFluidState(mutable).is(Tags.Fluids.WATER)) {
                                        level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2 | 16 | 32);
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (!this.explosionStarted) {
                LOGGER.debug("[{}] starting explosion", this.getId());
                LevelActiveExplosions.get(level).add(
                        new PlagiarizedExplosionHandlerBatched(
                                level,
                                this.getBlockX(),
                                this.getBlockY(),
                                this.getBlockZ(),
                                this.nukeType.getBlastStrength(),
                                BMNWServerConfig.explosionCalculationFactor(),
                                this.nukeType.getBlastRadius(),
                                this.getUUID()
                        )
                );
                this.explosionStarted = true;
                return;
            }
            if (!this.explosionFinished) {
                return;
            }
            if (!this.radiationReleased) {
                if (this.nukeType.getReleasedRadiation() > 0.0F) {
                    level.addFreshEntity(new RadiationLingerEntity(level, this.position(), this.nukeType));
                }
                this.setBiome();
                this.radiationReleased = true;
                return;
            }
            if (!this.leavesDestroyed) {
                if (this.orderedChunks == null) {
                    LOGGER.debug("[{}] starting leave destruction", this.getId());
                    int c = (this.nukeType.getDestroyedLeavesRadius() + 15) >> 4;
                    this.affectedMinCX = this.chunkPosition().x - c;
                    this.affectedMaxCX = this.chunkPosition().x + c;
                    this.affectedMinCZ = this.chunkPosition().z - c;
                    this.affectedMaxCZ = this.chunkPosition().z + c;
                    this.orderedChunks = new ArrayList<>();
                    for (int x = this.affectedMinCX; x <= this.affectedMaxCX; x++) {
                        for (int z = this.affectedMinCZ; z <= this.affectedMaxCZ; z++) {
                            this.orderedChunks.add(new ChunkPos(x, z));
                        }
                    }

                    this.orderedChunks.sort((cp1, cp2) -> {
                        int d1 = this.chunkPosition().getChessboardDistance(cp1);
                        int d2 = this.chunkPosition().getChessboardDistance(cp2);

                        return d1 - d2;
                    });
                    return;
                }
                if (this.orderedChunks.isEmpty()) {
                    this.orderedChunks = null;
                    this.leavesDestroyed = true;
                } else {
                    int max = Math.max(BMNWServerConfig.explosionCalculationFactor() / 100, 1);
                    for (int i = 0; (i < max && !this.orderedChunks.isEmpty()); i++) {
                        ChunkPos pos = this.orderedChunks.removeFirst();
                        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                        int y;
                        int d2 = this.nukeType.getDestroyedLeavesRadius() * this.nukeType.getDestroyedLeavesRadius();
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                {
                                    int x1 = (x + pos.getMinBlockX() - this.getBlockX());
                                    int z1 = (z + pos.getMinBlockZ() - this.getBlockZ());
                                    int sqr = x1 * x1 + z1 * z1;
                                    if (sqr > d2) continue;
                                    if (sqr == d2 && this.random.nextBoolean()) continue;
                                }
                                for (y = level.getSeaLevel(); y < level.getMaxBuildHeight(); y++) {
                                    mutable.set(pos.getBlockX(x), y, pos.getBlockZ(z));
                                    if (this.plant.contains(level.getBlockState(mutable).getBlock())) {
                                        level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2 | 16 | 32);
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (!this.treesCharred) {
                if (this.orderedChunks == null) {
                    LOGGER.debug("[{}] starting charred trees", this.getId());
                    int c = (this.nukeType.getCharredTreesRadius() + 15) >> 4;
                    this.affectedMinCX = this.chunkPosition().x - c;
                    this.affectedMaxCX = this.chunkPosition().x + c;
                    this.affectedMinCZ = this.chunkPosition().z - c;
                    this.affectedMaxCZ = this.chunkPosition().z + c;
                    this.orderedChunks = new ArrayList<>();
                    for (int x = this.affectedMinCX; x <= this.affectedMaxCX; x++) {
                        for (int z = this.affectedMinCZ; z <= this.affectedMaxCZ; z++) {
                            this.orderedChunks.add(new ChunkPos(x, z));
                        }
                    }

                    this.orderedChunks.sort((cp1, cp2) -> {
                        int d1 = this.chunkPosition().getChessboardDistance(cp1);
                        int d2 = this.chunkPosition().getChessboardDistance(cp2);

                        return d1 - d2;
                    });
                    return;
                }
                if (this.orderedChunks.isEmpty()) {
                    this.orderedChunks = null;
                    this.treesCharred = true;
                } else {
                    int max = Math.max(BMNWServerConfig.explosionCalculationFactor() / 100, 1);
                    for (int i = 0; (i < max && !this.orderedChunks.isEmpty()); i++) {
                        ChunkPos pos = this.orderedChunks.removeFirst();
                        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                        int y;
                        int d2 = this.nukeType.getCharredTreesRadius() * this.nukeType.getCharredTreesRadius();
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                {
                                    int x1 = (x + pos.getMinBlockX() - this.getBlockX());
                                    int z1 = (z + pos.getMinBlockZ() - this.getBlockZ());
                                    int sqr = x1 * x1 + z1 * z1;
                                    if (sqr > d2) continue;
                                    if (sqr == d2 && this.random.nextBoolean()) continue;
                                }
                                for (y = level.getSeaLevel(); y < level.getMaxBuildHeight(); y++) {
                                    mutable.set(pos.getBlockX(x), y, pos.getBlockZ(z));
                                    BlockState state = level.getBlockState(mutable);
                                    if (this.logs.contains(state.getBlock())) {
                                        level.setBlock(mutable, BMNWBlocks.CHARRED_LOG.get().defaultBlockState(), 2 | 16 | 32);
                                    } else if (this.planks.contains(state.getBlock())) {
                                        level.setBlock(mutable, BMNWBlocks.CHARRED_PLANKS.get().defaultBlockState(), 2 | 16 | 32);
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (!this.nuclearRemainsPlaced) {
                if (this.orderedChunks == null) {
                    if (this.nukeType.getNuclearRemainsRadius() <= 0) {
                        this.nuclearRemainsPlaced = true;
                        return;
                    }
                    LOGGER.debug("[{}] starting nuclear remains", this.getId());
                    int c = (this.nukeType.getNuclearRemainsRadius() + 31) >> 4;
                    this.affectedMinCX = this.chunkPosition().x - c;
                    this.affectedMaxCX = this.chunkPosition().x + c;
                    this.affectedMinCZ = this.chunkPosition().z - c;
                    this.affectedMaxCZ = this.chunkPosition().z + c;
                    this.orderedChunks = new ArrayList<>();
                    for (int x = this.affectedMinCX; x <= this.affectedMaxCX; x++) {
                        for (int z = this.affectedMinCZ; z <= this.affectedMaxCZ; z++) {
                            this.orderedChunks.add(new ChunkPos(x, z));
                        }
                    }

                    this.orderedChunks.sort((cp1, cp2) -> {
                        int d1 = this.chunkPosition().getChessboardDistance(cp1);
                        int d2 = this.chunkPosition().getChessboardDistance(cp2);

                        return d1 - d2;
                    });
                    return;
                }
                if (this.orderedChunks.isEmpty()) {
                    this.orderedChunks = null;
                    this.nuclearRemainsPlaced = true;
                } else {
                    int max = Math.max(BMNWServerConfig.explosionCalculationFactor() / 20, 1);
                    for (int i = 0; (i < max && !this.orderedChunks.isEmpty()); i++) {
                        ChunkPos pos = this.orderedChunks.removeFirst();
                        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                        int y;
                        int d = this.nukeType.getNuclearRemainsRadius();
                        int d2 = d * d;
                        int d3 = (d + 16) * (d + 16);
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                {
                                    int x1 = (x + pos.getMinBlockX() - this.getBlockX());
                                    int z1 = (z + pos.getMinBlockZ() - this.getBlockZ());
                                    int sqr = x1 * x1 + z1 * z1;
                                    if (sqr > d3) continue;
                                    if (sqr > d2 && this.random.nextBoolean()) continue;
                                }
                                y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x + pos.getMinBlockX(), z + pos.getMinBlockZ()) - 1;
                                mutable.set(x + pos.getMinBlockX(), y, z + pos.getMinBlockZ());
                                if (!level.isInWorldBounds(mutable)) continue;
                                BlockState state;
                                if (this.validNuclearRemainsState(state = level.getBlockState(mutable), 250.0F)) {
                                    if (this.coals.contains(state.getBlock())) {
                                        level.setBlock(mutable,
                                                this.random.nextFloat() < 0.05F ?
                                                        Blocks.EMERALD_ORE.defaultBlockState() :
                                                        Blocks.DIAMOND_ORE.defaultBlockState(), 3
                                        );
                                    } else {
                                        level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                                    }
                                    mutable.move(0, -1, 0);
                                    if (this.validNuclearRemainsState(state = level.getBlockState(mutable), 150.0F)) {
                                        if (this.coals.contains(state.getBlock())) {
                                            level.setBlock(mutable,
                                                    this.random.nextFloat() < 0.05F ?
                                                            Blocks.EMERALD_ORE.defaultBlockState() :
                                                            Blocks.DIAMOND_ORE.defaultBlockState(), 3
                                            );
                                        } else {
                                            level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                                        }
                                        mutable.move(0, -1, 0);
                                        if (this.validNuclearRemainsState(state = level.getBlockState(mutable), 50.0F)) {
                                            if (this.coals.contains(state.getBlock())) {
                                                level.setBlock(mutable,
                                                        this.random.nextFloat() < 0.05F ?
                                                                Blocks.EMERALD_ORE.defaultBlockState() :
                                                                Blocks.DIAMOND_ORE.defaultBlockState(), 3
                                                );
                                            } else {
                                                level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (!this.waterRefilled) {
                if (this.orderedChunks == null) {
                    LOGGER.debug("[{}] starting refill", this.getId());
                    int c = (this.nukeType.getBlastRadius() + 15) >> 4;
                    this.affectedMinCX = this.chunkPosition().x - c;
                    this.affectedMaxCX = this.chunkPosition().x + c;
                    this.affectedMinCZ = this.chunkPosition().z - c;
                    this.affectedMaxCZ = this.chunkPosition().z + c;
                    this.orderedChunks = new ArrayList<>();
                    ChunkPos.rangeClosed(new ChunkPos(this.affectedMinCX, this.affectedMinCZ), new ChunkPos(this.affectedMaxCX, this.affectedMaxCZ))
                            .forEach(this.orderedChunks::add);
                    return;
                }
                if (this.orderedChunks.isEmpty()) {
                    this.orderedChunks = null;
                    this.waterRefilled = true;
                } else {
                    ChunkPos pos = this.orderedChunks.removeFirst();
                    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                    int y;
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (y = this.evaporationMax - 1; y >= this.evaporationMin; y--) {
                                mutable.set(pos.getBlockX(x), y, pos.getBlockZ(z));
                                BlockState state = level.getBlockState(mutable);
                                if (state.canBeReplaced(Fluids.WATER)) {
                                    if (this.checkWater(level, mutable)) {
                                        level.setBlock(mutable, Blocks.WATER.defaultBlockState(), 2 | 16 | 32);
                                    }
                                }
                            }
                        }
                    }
                }
                return;
            }

            LOGGER.debug("Explosion with ID {} finished in {}ms", this.getId(), System.currentTimeMillis() - this.start);
            this.discard();
        }
    }

    private void setBiome() {
        if (this.level() instanceof ServerLevel level) {
            int min = this.nukeType.getMinimalBiomeRadius();
            if (min > 1) {
                BMNWBiomes.fillBiomeCylindrical(level, this.blockPosition().below(min), min*2,
                        min, BMNWBiomes.nuclear_wastes_minimal(level));
            }
            int nor = this.nukeType.getNormalBiomeRadius();
            if (nor > 1) {
                BMNWBiomes.fillBiomeCylindrical(level, this.blockPosition().below(nor), nor*2,
                        nor, BMNWBiomes.nuclear_wastes(level));
            }
            int sev = this.nukeType.getSevereBiomeRadius();
            if (sev > 1) {
                BMNWBiomes.fillBiomeCylindrical(level, this.blockPosition().below(sev), sev*2,
                        sev, BMNWBiomes.nuclear_wastes_severe(level));
            }
        }
    }

    private boolean checkWater(Level level, BlockPos pos) {
        return level.getFluidState(this.mutable1.setWithOffset(pos, 0, 1, 0)).is(Tags.Fluids.WATER)
                || level.getFluidState(this.mutable1.setWithOffset(pos, -1, 0, 0)).is(Tags.Fluids.WATER)
                || level.getFluidState(this.mutable1.setWithOffset(pos, 0, 0, -1)).is(Tags.Fluids.WATER)
                || level.getFluidState(this.mutable1.setWithOffset(pos, 1, 0, 0)).is(Tags.Fluids.WATER)
                || level.getFluidState(this.mutable1.setWithOffset(pos, 0, 0, 1)).is(Tags.Fluids.WATER);
    }

    private boolean validNuclearRemainsState(BlockState state, float resistance) {
        if (state.getBlock().getExplosionResistance() >= resistance) return false;
        if (state.canBeReplaced()) return false;
        if (!state.getFluidState().isEmpty()) return false;
        if (this.coals.contains(state.getBlock())) return true;
        return !state.is(BMNWTags.Blocks.NUCLEAR_REMAINS_BLACKLIST);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void notifyExplosionFinished() {
        System.out.println("hi");
        this.explosionFinished = true;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.age);
        buffer.writeByte(BMNWResourceKeys.NUKE_TYPE_REGISTRY.getId(this.nukeType));
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.age = additionalData.readInt();
        this.nukeType = BMNWResourceKeys.NUKE_TYPE_REGISTRY.byId(additionalData.readUnsignedByte());
    }
}
