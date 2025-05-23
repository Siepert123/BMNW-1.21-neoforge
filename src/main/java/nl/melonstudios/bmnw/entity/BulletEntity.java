package nl.melonstudios.bmnw.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class BulletEntity extends Entity {
    public BulletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected abstract void onImpact(Entity entity);

    protected boolean removeOnBlockCollision = true;
    protected int removalDelay = 24000;

    private int damageDelay = 1;
    public int getDamageDelay() {
        return this.damageDelay;
    }

    @Override
    public void tick() {
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.removeOnBlockCollision) {
            if (this.horizontalCollision || this.verticalCollision) {
                this.setRemoved(RemovalReason.KILLED);
                return;
            }
        }
        if (this.damageDelay == 0) {
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox());
            for (Entity entity : entities) {
                if (!BulletEntity.class.isAssignableFrom(entity.getClass())) {
                    this.onImpact(entity);
                    if (this.isRemoved()) break;
                }
            }
        } else this.damageDelay--;
        if (this.removalDelay == 0) this.setRemoved(RemovalReason.KILLED);
        else this.removalDelay--;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setDeltaMovement(
                compound.getDouble("delX"),
                compound.getDouble("delY"),
                compound.getDouble("delZ")
        );

        this.damageDelay = compound.getByte("damageDelay");
        this.removalDelay = compound.getInt("removalDelay");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putDouble("delX", this.getDeltaMovement().x);
        compound.putDouble("delY", this.getDeltaMovement().y);
        compound.putDouble("delZ", this.getDeltaMovement().z);

        compound.putByte("damageDelay", (byte) this.damageDelay);
        compound.putInt("removalDelay", this.removalDelay);
    }
}
