package nl.melonstudios.bmnw.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWDamageSources;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;

public class SimpleBulletEntity extends BulletEntity {
    public static final EntityDataAccessor<Float> DAMAGE_DATA = SynchedEntityData.defineId(SimpleBulletEntity.class, EntityDataSerializers.FLOAT);
    protected float damage;

    public SimpleBulletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public SimpleBulletEntity(Level level, double x, double y, double z, Vec3 heading, float damage) {
        this(BMNWEntityTypes.SIMPLE_BULLET.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(heading);
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE_DATA, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.damage = compound.getFloat("bulletDamage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putFloat("bulletDamage", this.damage);
    }

    @Override
    protected void onImpact(Entity entity) {
        entity.hurt(BMNWDamageSources.shot(this.level()), this.damage);
        this.setRemoved(RemovalReason.KILLED);
    }
}
