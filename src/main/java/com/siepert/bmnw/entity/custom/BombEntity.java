package com.siepert.bmnw.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BombEntity extends Entity {
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
    public static final boolean USE_RAY = true;
    protected void recalcPos() {
        worldPosition = getOnPos(0.5f);
    }
    protected static final Logger LOGGER = LogManager.getLogger("BMNW Nuclear Bomb");
    public BombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        recalcPos();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PROGRESS_DATA, progress);
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
    }

    @Override
    public void kill() {
        disableSkyLight();
        this.remove(RemovalReason.DISCARDED);
        this.gameEvent(GameEvent.ENTITY_DIE);
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
}
