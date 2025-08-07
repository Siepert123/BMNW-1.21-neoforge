package nl.melonstudios.bmnw.weapon.missile.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import nl.melonstudios.bmnw.interfaces.ICanBeCasted;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractMissileEntity extends Entity implements ICanBeCasted, IEntityWithComplexSpawn {
    public static final EntityDataAccessor<Integer> HEALTH_DATA =
            SynchedEntityData.defineId(AbstractMissileEntity.class, EntityDataSerializers.INT);

    public AbstractMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public int hp = 69;

    @Override
    public void tick() {
        Level level = this.level();
        this.syncValues(level.isClientSide);

        if (this.onGround()) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            if (this.hasThrust()) {
                this.setDeltaMovementAccordingly();
                this.spawnThrustParticles();
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1, 0.9));
                if (this.getDeltaMovement().y > -2) {
                    this.addDeltaMovement(new Vec3(0, -0.05, 0));
                }
                if (this.getXRot() > -70) {
                    this.setXRot(Mth.clamp(this.getXRot() - 5, -90, 90));
                }
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setOnGround(this.horizontalCollision || this.verticalCollision);
        }
        if (!level.isClientSide && this.shouldDetonate()) {
            this.discard();
            this.onImpact();
        }
    }

    protected abstract void setDeltaMovementAccordingly();
    protected abstract boolean shouldDetonate();

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HEALTH_DATA, 69);
    }

    protected final void syncValues(boolean clientSide) {
        this.syncAdditionalValues(clientSide);
        if (clientSide) {
            this.hp = this.entityData.get(HEALTH_DATA);
        } else {
            this.entityData.set(HEALTH_DATA, this.hp);
        }
    }
    protected void syncAdditionalValues(boolean clientSide) {}

    @Override
    protected final void readAdditionalSaveData(CompoundTag compound) {
        this.readNBT(compound);

        this.hp = compound.getInt("hp");
    }

    @Override
    protected final void addAdditionalSaveData(CompoundTag compound) {
        this.writeNBT(compound);

        compound.putInt("hp", this.hp);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        int dmg = Mth.floor(amount);
        this.hp -= dmg;
        this.onDamaged(source, dmg);
        if (this.hp <= 0) {
            this.remove(RemovalReason.KILLED);
            this.onDestroyed(source);
        }
        return true;
    }

    protected boolean hasThrust() {
        return true;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {}
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {}

    protected abstract int getMaxHealth();

    protected abstract void writeNBT(CompoundTag nbt);
    protected abstract void readNBT(CompoundTag nbt);

    protected abstract void onImpact();
    protected abstract void onDamaged(DamageSource source, int dmg);
    protected abstract void onDestroyed(DamageSource source);

    protected void spawnThrustParticles() {}
}
