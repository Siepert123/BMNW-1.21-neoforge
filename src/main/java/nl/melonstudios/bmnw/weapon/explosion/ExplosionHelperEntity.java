package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWTags;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.weapon.RadiationLingerEntity;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;
import nl.melonstudios.bmnw.wifi.PacketMushroomCloud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ExplosionHelperEntity extends Entity {
    public ExplosionHelperEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ExplosionHelperEntity(Level level, Vec3 pos, NukeType type) {
        this(BMNWEntityTypes.EXPLOSION_HELPER.get(), level);
        this.setPos(pos);
        this.nukeType = Objects.requireNonNull(type);
    }

    private NukeType nukeType;

    private int affectedMinCX, affectedMinCZ;
    private int affectedMaxCX, affectedMaxCZ;

    private List<ChunkPos> orderedChunks = null;

    private boolean hasSpawnedCloud = false;
    private boolean explosionStarted = false;
    private boolean explosionFinished = false;
    private boolean leavesDestroyed = false;
    private boolean treesCharred = false;
    private boolean radiationReleased = false;
    private boolean nuclearRemainsPlaced = false;
    private boolean waterRefilled = false;

    @Override
    public void tick() {
        Level level = this.level();
        if (this.nukeType == null || level.isClientSide) return;

        if (!this.hasSpawnedCloud) {
            PacketDistributor.sendToPlayersInDimension((ServerLevel) level,
                    new PacketMushroomCloud(
                            false,
                            this.getX(), this.getY(), this.getZ(),
                            this.nukeType.getMushroomCloudSize()
                    )
            );
            this.hasSpawnedCloud = true;
            return;
        }
        if (!this.explosionStarted) {
            Exploder.ALL.add(
                    new PlagiarizedExplosionHandlerBatched(
                            level,
                            this.getBlockX(),
                            this.getBlockY(),
                            this.getBlockZ(),
                            this.nukeType.getBlastRadius()*2,
                            BMNWServerConfig.explosionCalculationFactor(),
                            this.nukeType.getBlastRadius()
                    ).setOnFinish(
                            () -> this.explosionFinished = true
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
            this.radiationReleased = true;
            return;
        }
        if (!this.leavesDestroyed) {
            this.leavesDestroyed = true;
            return;
        }
        if (!this.treesCharred) {
            this.treesCharred = true;
            return;
        }
        if (!this.nuclearRemainsPlaced) {
            if (this.orderedChunks == null) {
                int c = (this.nukeType.getNuclearRemainsRadius()+15) >> 4;
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
                ChunkPos pos = this.orderedChunks.removeFirst();
                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                int y;
                int d2 = this.nukeType.getNuclearRemainsRadius()*this.nukeType.getNuclearRemainsRadius();
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        {
                            int x1 = (x+pos.getMinBlockX()-this.getBlockX());
                            int z1 = (z+pos.getMinBlockZ()-this.getBlockZ());
                            int sqr = x1 * x1 + z1 * z1;
                            if (sqr > d2) continue;
                            if (sqr == d2 && this.random.nextBoolean()) continue;
                        }
                        y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x+pos.getMinBlockX(), z+pos.getMinBlockZ())-1;
                        mutable.set(x+pos.getMinBlockX(), y, z+pos.getMinBlockZ());
                        if (!level.isInWorldBounds(mutable)) continue;
                        if (validNuclearRemainsState(level.getBlockState(mutable), 250.0F)) {
                            level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                            mutable.move(0, -1, 0);
                            if (validNuclearRemainsState(level.getBlockState(mutable), 150.0F)) {
                                level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                                mutable.move(0, -1, 0);
                                if (validNuclearRemainsState(level.getBlockState(mutable), 50.0F)) {
                                    level.setBlock(mutable, BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        if (!this.waterRefilled) {
            this.waterRefilled = true;
            return;
        }

        this.discard();
    }

    private static boolean validNuclearRemainsState(BlockState state, float resistance) {
        if (state.getBlock().getExplosionResistance() >= resistance) return false;
        if (state.canBeReplaced()) return false;
        if (!state.getFluidState().isEmpty()) return false;
        return !state.is(BMNWTags.Blocks.NUCLEAR_REMAINS_BLACKLIST);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("nukeType", Objects.requireNonNull(BMNWResourceKeys.NUKE_TYPE_REGISTRY.getKey(this.nukeType)).toString());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.nukeType = BMNWResourceKeys.NUKE_TYPE_REGISTRY.get(ResourceLocation.parse(compoundTag.getString("nukeType")));
    }
}
