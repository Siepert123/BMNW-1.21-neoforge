package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MissileEntity extends Entity {
    protected static final Logger LOGGER = LogManager.getLogger();

    public static final EntityDataAccessor<Boolean> IS_FALLING_DATA = SynchedEntityData.defineId(MissileEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean falling = false;
    public BlockPos launchPos = getOnPos();
    public MissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public void setLaunchPos(BlockPos pos) {
        launchPos = pos;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IS_FALLING_DATA, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        falling = compound.getBoolean("falling");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("falling", falling);
    }

    @Override
    public void baseTick() {
        if (level().isClientSide()) {
            falling = entityData.get(IS_FALLING_DATA);
            level().addParticle(ModParticleTypes.FIRE_SMOKE.get(), this.getX(), this.getY(), this.getZ(), 0, falling ? 0.1 : -0.1, 0);
        } else {
            if (falling) {
                if (!level().getBlockState(getOnPos()).isAir()) {
                    NuclearChargeEntity entity = new NuclearChargeEntity(ModEntityTypes.NUCLEAR_CHARGE.get(), level());
                    entity.setPos(this.getPosition(0));
                    level().addFreshEntity(entity);
                    this.kill();
                } else {
                    this.move(MoverType.SELF, new Vec3(0, -1, 0));
                }
            } else {
                this.move(MoverType.SELF, new Vec3(0, 1, 0));

                if (this.getY() > 256) {
                    falling = true;
                    this.setPos(0, 256, 0);
                }
            }
            entityData.set(IS_FALLING_DATA, falling);
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
}
