package nl.melonstudios.bmnw.entity.missile;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.interfaces.ICanBeCasted;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractMissileEntity extends Entity implements ICanBeCasted {
    public static final EntityDataAccessor<Integer> HEALTH_DATA =
            SynchedEntityData.defineId(AbstractMissileEntity.class, EntityDataSerializers.INT);

    public AbstractMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public int hp = 50;

    @Override
    public void tick() {
        Level level = this.level();
        this.syncValues(level.isClientSide);

        if (this.hasThrust()) {
        } else {
            if (this.getDeltaMovement().y > -2) {
                this.addDeltaMovement(new Vec3(0, -0.05, 0));
            }
        }
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
            this.onDestroyed(source);
            this.remove(RemovalReason.KILLED);
        }
        return true;
    }

    protected boolean hasThrust() {
        return true;
    }

    protected abstract void writeNBT(CompoundTag nbt);
    protected abstract void readNBT(CompoundTag nbt);

    protected abstract void onImpact();
    protected abstract void onDamaged(DamageSource source, int dmg);
    protected abstract void onDestroyed(DamageSource source);
}
