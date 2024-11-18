package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.misc.ModTags;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Abstract entity class for all entities that represent an explosion.
 * Contains various utility tools.
 * @see NuclearChargeEntity
 * @see DudEntity
 */
public abstract class BombEntity extends Entity {
    public static final EntityDataAccessor<Integer> PROGRESS_DATA = SynchedEntityData.defineId(BombEntity.class, EntityDataSerializers.INT);
    public int progress = 0;
    protected BlockPos worldPosition = getOnPos(0.5f);
    protected boolean shouldLightSky = false;
    protected void enableSkyLight() {
        shouldLightSky = true;
    }
    protected void disableSkyLight() {
        shouldLightSky = false;
    }
    protected int thunderSFXInterval = 0;
    protected void setThunderSFXInterval(int interval) {
        thunderSFXInterval = interval;
    }
    protected int getThunderSFXInterval() {
        return thunderSFXInterval;
    }
    protected int explosionSFXInterval = 0;
    protected void setExplosionSFXInterval(int interval) {
        explosionSFXInterval = interval;
    }
    protected int getExplosionSFXInterval() {
        return explosionSFXInterval;
    }
    public static final boolean USE_RAY = true;
    protected void recalcPos() {
        worldPosition = getOnPos(1f).above();
    }
    protected static final Logger LOGGER = LogManager.getLogger("BMNW Nuclear Bomb");
    public BombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        recalcPos();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PROGRESS_DATA, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        progress = compound.getInt("progress");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("progress", progress);
    }

    @Override
    public void baseTick() {
        if (shouldLightSky) {
            level().setSkyFlashTime(5);
        }
        if (getThunderSFXInterval() != 0) {
            if (level().isClientSide()) {
                if (level().getGameTime() % getThunderSFXInterval() == 0) {
                    level().playLocalSound(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            SoundEvents.LIGHTNING_BOLT_THUNDER,
                            SoundSource.AMBIENT,
                            10000.0F,
                            0.8F + this.random.nextFloat() * 0.2F,
                            false
                    );
                }
            }
        }
        if (getExplosionSFXInterval() != 0) {
            if (level().isClientSide()) {
                if (level().getGameTime() % getThunderSFXInterval() == 0) {
                    level().playLocalSound(
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            SoundEvents.GENERIC_EXPLODE.value(),
                            SoundSource.AMBIENT,
                            10000.0F,
                            0.8F + this.random.nextFloat() * 0.2F,
                            false
                    );
                }
            }
        }
    }

    @Override
    public void kill() {
        disableSkyLight();
        this.remove(RemovalReason.DISCARDED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide()) super.remove(reason);
        else {
            if (reason == RemovalReason.UNLOADED_TO_CHUNK || reason == RemovalReason.UNLOADED_WITH_PLAYER) {
                return;
            } else {
                super.remove(reason);
            }
        }
    }

    protected static Vec3 convertVec3i(Vec3i vec) {
        return new Vec3(vec.getX() + 0.5, vec.getY() + 0.5, vec.getZ() + 0.5);
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public void updateSwimming() {

    }

    @Override
    public void updateFluidHeightAndDoFluidPushing() {

    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    protected void effectEntities(int radius) {
        List<Entity> entities = level().getEntitiesOfClass(Entity.class, new AABB(
                convertVec3i(worldPosition.offset(-radius, -radius, -radius)),
                convertVec3i(worldPosition.offset(radius, radius, radius))
        ));

        for (Entity entity : entities) {
            if (Math.sqrt(entity.distanceToSqr(convertVec3i(worldPosition))) <= radius) {
                entity.setRemainingFireTicks(36000);
            }
        }
    }

    protected void irradiate(int nuclearRadius, int grassRadius, long femtoRadsInserted) {
        final BlockState remains = ModBlocks.NUCLEAR_REMAINS.get().defaultBlockState();
        final BlockState diamond = Blocks.DIAMOND_ORE.defaultBlockState();
        final BlockState emerald = Blocks.EMERALD_ORE.defaultBlockState();
        for (int x = -nuclearRadius; x <= nuclearRadius; x++) {
            for (int z = -nuclearRadius; z <= nuclearRadius; z++) {
                for (int y = nuclearRadius; y >= -nuclearRadius; y--) {
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).isAir() || level().getBlockState(pos).canBeReplaced()) continue;
                    final double sqrt = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2));
                    if (sqrt > nuclearRadius) continue;
                    final float str = nuclearRemainsReplaceable();
                    if (USE_RAY) {
                        BlockHitResult hitResult = level().clip(new ClipContext(convertVec3i(worldPosition), convertVec3i(pos),
                                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
                        BlockState state = level().getBlockState(hitResult.getBlockPos());
                        if (state.is(Tags.Blocks.ORES_COAL)) {
                            if (level().random.nextFloat() > 0.95) {
                                level().setBlock(hitResult.getBlockPos(), emerald, 3);
                            } else if (level().random.nextFloat() > 0.05) {
                                level().setBlock(hitResult.getBlockPos(), diamond, 3);
                            }
                        } else if (state.getBlock().getExplosionResistance() <= str) {
                            if (!state.is(ModTags.Blocks.NUCLEAR_REMAINS_BLACKLIST)) {
                                level().setBlock(hitResult.getBlockPos(), remains, 3);
                            }
                        }
                    } else {
                        if (level().getBlockState(pos).getBlock().getExplosionResistance() >= nuclearRadius - sqrt)
                            continue;
                        level().setBlock(pos, remains, 3);
                    }
                }
            }
        }

        final BlockState grass = ModBlocks.IRRADIATED_GRASS_BLOCK.get().defaultBlockState();
        for (int x = -grassRadius; x <= grassRadius; x++) {
            for (int z = -grassRadius; z <= grassRadius; z++) {
                for (int y = grassRadius; y >= -grassRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2)) > grassRadius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).isAir()) continue;
                    if (level().getBlockState(pos).canBeReplaced()) level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    if (level().getBlockState(pos).is(ModTags.Blocks.IRRADIATABLE_GRASS_BLOCKS)) level().setBlock(pos, grass, 3);
                }
            }
        }

        RadHelper.insertRadiation(level(), worldPosition, femtoRadsInserted);
    }
    protected void dry(int radius) {
        final BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = radius; y >= -radius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) > radius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (!level().getFluidState(pos).isEmpty()) {
                        level().setBlock(pos, air, 3);
                    }
                    if (level().getBlockState(pos).is(ModTags.Blocks.MELTABLES)) {
                        level().setBlock(pos, air, 3);
                    }
                }
            }
        }
    }
    protected void burn(int radius) {
        final BlockState log = ModBlocks.CHARRED_LOG.get().defaultBlockState();
        final BlockState plank = ModBlocks.CHARRED_PLANKS.get().defaultBlockState();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = radius; y >= -radius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) > radius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                        level().setBlock(pos, log, 3);
                    }
                    if (level().getBlockState(pos).is(BlockTags.LEAVES)) {
                        level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                    if (level().getBlockState(pos).is(ModTags.Blocks.CHARRABLE_PLANKS)) {
                        level().setBlock(pos, plank, 3);
                    }
                }
            }
        }
    }
    protected void inflammate(int radius) {
        //if (level().isClientSide()) return;
        final BlockState fire = Blocks.FIRE.defaultBlockState();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = radius; y >= radius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) > radius) continue;
                    if (level().random.nextFloat() > 0.99) {
                        BlockPos pos = worldPosition.offset(x, y, z);
                        if (level().getBlockState(pos).canBeReplaced() &&
                                level().getBlockState(pos.below()).isFaceSturdy(level(), pos.below(), Direction.UP, SupportType.FULL)) {
                            level().setBlock(pos, fire, 3);
                        }
                    }
                }
            }
        }
    }

    protected float blastStrMultiplier = 0.5f;
    protected void setBlastStrMultiplier(float multiplier) {
        this.blastStrMultiplier = multiplier;
    }

    protected float getBlastStrength(int progress, int radius) {
        return (radius - progress) * blastStrMultiplier;
    }

    protected float nuclearRemainsReplaceable() {
        return blastStrMultiplier * 20;
    }
}
