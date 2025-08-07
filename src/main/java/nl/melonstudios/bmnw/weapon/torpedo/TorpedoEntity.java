package nl.melonstudios.bmnw.weapon.torpedo;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.interfaces.ICanBeCasted;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TorpedoEntity extends Entity implements IEntityWithComplexSpawn, ICanBeCasted {
    public static final EntityDataAccessor<Byte> DATA_HEALTH =
            SynchedEntityData.defineId(TorpedoEntity.class, EntityDataSerializers.BYTE);
    public TorpedoEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public TorpedoEntity(Level level, Vec3 pos, TorpedoStats stats) {
        this(BMNWEntityTypes.TORPEDO.get(), level);
        this.setPos(pos);
        this.torpedoStats = stats;
    }
    public TorpedoEntity setHeading(float y, float p) {
        this.yaw = y;
        this.pitch = p;
        return this;
    }

    private byte health = 20;
    private TorpedoStats torpedoStats = TorpedoStats.EMPTY;
    private float yaw, pitch;

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.health = this.entityData.get(DATA_HEALTH);
        } else {
            this.entityData.set(DATA_HEALTH, this.health);
        }
        boolean valid = this.torpedoStats.isValid();
        float efficiency = valid ? this.torpedoStats.thruster().efficiency() : 0.0F;
        float inaccuracy = valid ? this.torpedoStats.fins().inaccuracy() : 0.0F;
        float speeding = efficiency * 3;
        Vec3 heading = this.calculateViewVector(
                this.yaw + this.random.nextFloat() * inaccuracy - (inaccuracy * 0.5F),
                this.pitch + this.random.nextFloat() * inaccuracy - (inaccuracy * 0.5F)
        ).scale(speeding);
        this.move(MoverType.SELF, heading);
        if (this.horizontalCollision || this.verticalCollision) {
            this.discard();
            if (valid) {
                Level level = Objects.requireNonNull(this.level());
                float explosionPower = this.torpedoStats.head().explosionPower();
                boolean fire = this.torpedoStats.head().fire();

                level.explode(this,
                        this.getX(), this.getY(), this.getZ(),
                        explosionPower, fire,
                        Level.ExplosionInteraction.MOB
                );
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            int amountI = (int) amount;
            this.health -= (byte)Math.min(amountI, this.health);
            if (this.health <= 0) {
                this.handleDestruction();
                this.discard();
            }
            return true;
        }
    }
    private void handleDestruction() {
        Level level = Objects.requireNonNull(this.level());
        float explosionPower = this.torpedoStats.head().explosionPower();
        boolean fire = this.torpedoStats.head().fire();

        level.explode(this,
                this.getX(), this.getY(), this.getZ(),
                explosionPower * 0.25F, fire,
                Level.ExplosionInteraction.MOB
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HEALTH, this.health);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("TorpedoStats", this.torpedoStats.write(new CompoundTag()));
        compound.putFloat("targetYaw", this.yaw);
        compound.putFloat("targetPitch", this.pitch);
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.torpedoStats = TorpedoStats.read(compound.getCompound("TorpedoStats"));
        this.yaw = compound.getFloat("targetYaw");
        this.pitch = compound.getFloat("targetPitch");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        this.torpedoStats.write(buf);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        this.torpedoStats = TorpedoStats.read(buf);
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }
}
