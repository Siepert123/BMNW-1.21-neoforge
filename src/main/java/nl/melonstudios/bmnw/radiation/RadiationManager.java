package nl.melonstudios.bmnw.radiation;

import nl.melonstudios.bmnw.hazard.HazardRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Deprecated(forRemoval = true)
public class RadiationManager {
    private static final Logger LOGGER = LogManager.getLogger("BMNW Radiation Manager");

    public static final String rad_nbt_tag = "bmnw_RAD";

    public static final float radiation_decay = 0.999f;
    public static final float lower_delete_limit = 0.00001f;
    public static final float distance_limit_sqr = 1048576;

    public static final float default_decay_air = 0.99f;
    public static final float default_decay_solid = 0.9f;

    private static RadiationManager instance;
    public static RadiationManager getInstance() {
        return instance;
    }

    public static void create(@Nonnull final MinecraftServer server) {
        LOGGER.info("Creating new RadiationManager");
        instance = new RadiationManager(server);
        instance.init();
        instance.loadAll(server);

    }
    public static void delete(@Nonnull final MinecraftServer server) {
        LOGGER.info("Deleting RadiationManager");

        instance.saveAll(server);
        instance.destroy();
        instance = null;
    }

    private final MinecraftServer server;
    private RadiationManager(@Nonnull final MinecraftServer server) {
        this.server = server;
    }

    private final Map<ResourceLocation, File> dimensionDataFiles = new HashMap<>();
    private final Map<ResourceLocation, List<RadiationSource>> radiationSources = new HashMap<>();

    public File getFileForLevel(@Nonnull final ServerLevel level) {
        return new File(dimensionDataFiles.get(level.dimension().location()), "bmnw_radiation.dat");
    }
    public File getFileForLevel(@Nonnull final ResourceLocation level) {
        return new File(dimensionDataFiles.get(level), "bmnw_radiation.dat");
    }

    private void init() {
        int c = 0;
        for (final ServerLevel level : server.getAllLevels()) {
            dimensionDataFiles.put(level.dimension().location(),
                    ObfuscationReflectionHelper.getPrivateValue(DimensionDataStorage.class, level.getDataStorage(), "dataFolder"));
            radiationSources.put(level.dimension().location(), new ArrayList<>());
            recalculateThesePlease.put(level.dimension().location(), new ArrayList<>());
            c++;
        }
        LOGGER.info("Obtained save folders of {} levels", c);
    }
    private void destroy() {
        dimensionDataFiles.clear();
        radiationSources.clear();
    }

    public void tick(@Nonnull final MinecraftServer server) {
        for (final ServerLevel level : server.getAllLevels()) tick(level);
    }
    private void tick(@Nonnull final ServerLevel level) {
        final ResourceLocation rsl = level.dimension().location();
        final List<BlockPos> toRemove = new ArrayList<>();
        for (final RadiationSource source : radiationSources.get(rsl)) {
            final BlockPos pos = source.pos;

            final List<Entity> entities = level.getEntities(null, source.box);

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                    float rads = source.getImpact(living);
                    if (rads < lower_delete_limit) continue;
                    addEntityRadiation(living, rads / 100);
                }
            }

            if (!source.persistent) {
                source.radiation *= radiation_decay;
                if (source.radiation < lower_delete_limit) toRemove.add(pos);
            }
        }
        for (BlockPos pos : toRemove) {
            radiationSources.get(rsl).removeIf((source) -> !source.persistent && source.pos == pos);
        }

        if (!recalculateThesePlease.get(rsl).isEmpty()) {
            List<ChunkAccess> rec = recalculateThesePlease.get(rsl);
            for (ChunkAccess c : rec) {
                recalculateChunkSources(c);
            }
            rec.clear();
            save(level.dimension().location());
        } else if ((level.getGameTime() & 0xfff) == 0xfff) {
            try {
                @SuppressWarnings("unchecked")
                Iterable<ChunkHolder> chunkHolders = (Iterable<ChunkHolder>)
                        ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks")
                                .invoke(level.getChunkSource().chunkMap);

                for (ChunkHolder chunkHolder : chunkHolders) {
                    LevelChunk chunk = chunkHolder.getTickingChunk();
                    if (chunk == null || !level.isLoaded(chunk.getPos().getMiddleBlockPosition(64))) continue;

                    this.recalculateChunkSources(chunk);
                }
            } catch (Throwable throwable) {
                LOGGER.error("Could not mass-recalculate chunks: {}: {}", throwable.getClass().getCanonicalName(), throwable.getLocalizedMessage());
            }
            save(level.dimension().location());
        }
    }

    private final Map<ResourceLocation, List<ChunkAccess>> recalculateThesePlease = new HashMap<>();

    public void addEntityRadiation(LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        float o = nbt.getFloat(rad_nbt_tag);
        nbt.putFloat(rad_nbt_tag, Math.max(o + rads, 0));
    }
    public void setEntityRadiation(LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        nbt.putFloat(rad_nbt_tag, rads);
    }

    public void putSource(ResourceLocation level, BlockPos pos, float radiation) {
        putSource(level, pos, radiation, true);
    }
    public void putSource(ResourceLocation level, BlockPos pos, float radiation, boolean persistent) {
        assert radiationSources.containsKey(level);
        radiationSources.get(level).add(new RadiationSource(pos, radiation, persistent));
    }
    //Can ONLY remove persistents!!
    public boolean removeSource(ResourceLocation level, BlockPos pos) {
        List<RadiationSource> sources = radiationSources.get(level);
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).persistent && sources.get(i).pos.equals(pos)) {
                sources.remove(i);
                return true;
            }
        }
        return false;
    }

    public int sourceCount(ResourceLocation level) {
        return radiationSources.get(level).size();
    }

    public void removeAllSources(ResourceLocation level) {
        radiationSources.get(level).clear();
    }

    public void saveAll(@Nonnull final MinecraftServer server) {
        for (final ServerLevel level : server.getAllLevels()) {
            save(level.dimension().location());
        }
    }
    public void save(@Nonnull final ResourceLocation level) {
        final File file = getFileForLevel(level);
        try (final FileOutputStream stream = new FileOutputStream(file)) {
            final CompoundTag data = new CompoundTag();

            final ListTag list = new ListTag();

            final List<RadiationSource> radiationSourceMap = radiationSources.get(level);

            for (final RadiationSource source : radiationSourceMap) {
                final BlockPos pos = source.pos;

                final CompoundTag tag = new CompoundTag();
                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
                tag.putBoolean("persistent", source.persistent);
                tag.putFloat("radiation", source.radiation);

                list.add(tag);
            }

            data.put("sources", list);

            NbtIo.writeCompressed(data, stream);
        } catch (final IOException e) {
            LOGGER.fatal("Failed to save radiation data to level {}", level);
            return;
        }
        LOGGER.info("Saved radiation data to level {}", level);
    }

    public void loadAll(@Nonnull final MinecraftServer server) {
        for (final ServerLevel level : server.getAllLevels()) {
            load(level.dimension().location());
        }
        setChunksAvailable(true);
    }
    public void load(@Nonnull final ResourceLocation level) {
        radiationSources.get(level).clear();
        final File file = getFileForLevel(level);
        if (file.exists()) {
            try (final FileInputStream stream = new FileInputStream(file)) {
                final CompoundTag data = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());

                if (data.contains("sources", Tag.TAG_LIST)) {
                    final ListTag list = data.getList("sources", Tag.TAG_COMPOUND);
                    for (final Tag tag : list) {
                        final CompoundTag nbt = (CompoundTag) tag;

                        final BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                        final RadiationSource source = new RadiationSource(pos, nbt.getFloat("radiation"), nbt.getBoolean("persistent"));

                        radiationSources.get(level).add(source);
                    }
                }
            } catch (final IOException e) {
                LOGGER.fatal("Failed to load radiation data from level {}", level);
                return;
            }
            LOGGER.info("Loaded radiation data from level {}", level);
        } else {
            LOGGER.info("No radiation data yet available for level {}", level);
        }
    }

    //This will lag maybe
    public void iterateBlocks(Vec3 start, Vec3 end, Consumer<BlockPos> action) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        int x = Mth.floor(start.x);
        int y = Mth.floor(start.y);
        int z = Mth.floor(start.z);

        int endX = Mth.floor(end.x);
        int endY = Mth.floor(end.y);
        int endZ = Mth.floor(end.z);

        int stepX = (dx > 0) ? 1 : (dx < 0) ? -1 : 0;
        int stepY = (dy > 0) ? 1 : (dy < 0) ? -1 : 0;
        int stepZ = (dz > 0) ? 1 : (dz < 0) ? -1 : 0;

        double tDeltaX = (dx != 0) ? 1 / Math.abs(dx) : Double.MAX_VALUE;
        double tDeltaY = (dy != 0) ? 1 / Math.abs(dy) : Double.MAX_VALUE;
        double tDeltaZ = (dz != 0) ? 1 / Math.abs(dz) : Double.MAX_VALUE;

        double nextBoundaryX = (stepX > 0) ? (x + 1) : x;
        double nextBoundaryY = (stepY > 0) ? (y + 1) : y;
        double nextBoundaryZ = (stepZ > 0) ? (z + 1) : z;

        double tMaxX = (dx != 0) ? (nextBoundaryX - start.x) / dx : Double.MAX_VALUE;
        double tMaxY = (dy != 0) ? (nextBoundaryY - start.y) / dy : Double.MAX_VALUE;
        double tMaxZ = (dz != 0) ? (nextBoundaryZ - start.z) / dz : Double.MAX_VALUE;

        while (true) {
            BlockPos pos = new BlockPos(x, y, z);
            action.accept(pos);

            if (x == endX && y == endY && z == endZ) break;

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    tMaxY += tDeltaY;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }
    }

    public void setChunksAvailable(boolean available) {
         chunksAvailable = available;
    }
    private boolean chunksAvailable = false;
    public boolean areChunksAvailable() {
        return chunksAvailable;
    }
    private static final List<ChunkAccess> queue = new ArrayList<>();
    public static void addToQueue(ChunkAccess c) {
        if (!queue.contains(c)) queue.add(c);
    }
    public void recalculateChunkSources(ChunkAccess chunk) {
        if (areChunksAvailable()) {
            ChunkPos pos = chunk.getPos();
            List<RadiationSource> bin = new ArrayList<>();
            for (RadiationSource source : radiationSources.get(chunk.getLevel().dimension().location())) {
                if (source.pos.getX() > pos.getMaxBlockX() || source.pos.getX() < pos.getMinBlockX()) continue;
                if (source.pos.getZ() > pos.getMaxBlockZ() || source.pos.getZ() < pos.getMinBlockZ()) continue;
                bin.add(source);
            }
            radiationSources.get(chunk.getLevel().dimension().location()).removeAll(bin);
            recalculate_chunk_sources(chunk);
            if (!queue.isEmpty()) {
                List<ChunkAccess> copy = new ArrayList<>(queue);
                queue.clear();
                for (ChunkAccess c : copy) {
                    recalculateChunkSources(c);
                }
            }
        } else {
            if (!queue.contains(chunk)) queue.add(chunk);
        }
    }
    private void recalculate_chunk_sources(ChunkAccess chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
                    BlockPos pos = chunk.getPos().getBlockAt(x, y, z);
                    BlockState state = chunk.getBlockState(pos);
                    if (HazardRegistry.getRadRegistry(state.getBlock()) > 0) {
                        this.putSource(chunk.getLevel().dimension().location(), pos, HazardRegistry.getRadRegistry(state.getBlock()));
                    }
                }
            }
        }
    }

    public static boolean exposedToAir(Level level, BlockPos pos) {
        int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        if (pos.getY() >= h-1) return true;
        for (Direction direction : Direction.values()) {
            BlockPos sidePos = pos.offset(direction.getNormal());
            int sideH = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sidePos.getX(), sidePos.getZ());
            if (pos.getY() >= sideH-1) return true;
        }
        return false;
    }
}
