package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
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

    public BlockPos launchPos = getOnPos();
    public MissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public void setLaunchPos(BlockPos pos) {
        launchPos = pos;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void baseTick() {
        if (this.getPosition(0).y() >= 128) {
            NuclearChargeEntity entity = new NuclearChargeEntity(ModEntityTypes.NUCLEAR_CHARGE.get(), level());
            entity.setPos(this.getPosition(0));
            level().addFreshEntity(entity);
            this.kill();
        } else {
            this.move(MoverType.SELF, this.getPosition(0).add(0, 1, 0));
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
