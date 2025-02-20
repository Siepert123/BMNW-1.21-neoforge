package nl.melonstudios.bmnw.entity;

import nl.melonstudios.bmnw.init.BMNWParticleTypes;
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
import org.joml.Vector2i;

public abstract class MissileEntity extends Entity {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected float speed = 1;
    public float getSpeed() {
        return speed;
    }

    public static final EntityDataAccessor<Boolean> IS_FALLING_DATA =
            SynchedEntityData.defineId(MissileEntity.class, EntityDataSerializers.BOOLEAN);

    protected Vector2i target = new Vector2i(0, 0);

    public void setTarget(Vector2i target) {
        this.target = target;
    }

    private boolean falling = false;
    public boolean isFalling() {
        return falling;
    }
    protected MissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(IS_FALLING_DATA, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        falling = compound.getBoolean("falling");

        target = new Vector2i(compound.getInt("targetX"), compound.getInt("targetZ"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("falling", falling);

        compound.putInt("targetX", target.x());
        compound.putInt("targetZ", target.y());
    }

    protected void createSmoke() {
        float vx = random.nextFloat() - random.nextFloat();
        float vy = random.nextFloat() - random.nextFloat();
        float vz = random.nextFloat() - random.nextFloat();

        level().addParticle(BMNWParticleTypes.LARGE_MISSILE_SMOKE.get(), true,
                getX() + vx, getY() + vy + (falling ? 2 : -2), getZ() + vz,
                0, 0, 0);
    }
    @Override
    public void baseTick() {
        if (level().isClientSide()) {
            falling = entityData.get(IS_FALLING_DATA);
            if (random.nextFloat() > 0.2) createSmoke();
            level().addParticle(BMNWParticleTypes.FIRE_SMOKE.get(), true,
                    this.getX(), this.getY(), this.getZ(), 0, falling ? 0.1 : -0.1, 0);
        } else {
            if (isFalling()) {
                if (!level().getBlockState(getOnPos()).isAir()) {
                    this.onImpact();
                    this.kill();
                }
            } else {
                if (!level().getBlockState(getOnPos().above(2)).isAir()) {
                    this.onImpact();
                    this.kill();
                }
            }
            entityData.set(IS_FALLING_DATA, falling);
        }
        if (falling) {
            this.move(MoverType.SELF, new Vec3(0, -speed, 0));

            if (this.getY() < -64) this.kill();
        } else {
            this.move(MoverType.SELF, new Vec3(0, speed, 0));

            if (this.getY() > 320) {
                falling = true;
                this.setPos(target.x(), 320, target.y());
            }
        }
    }

    protected abstract void onImpact();

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
