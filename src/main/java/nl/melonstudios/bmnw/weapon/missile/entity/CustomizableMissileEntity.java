package nl.melonstudios.bmnw.weapon.missile.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.weapon.missile.MissileBlueprint;
import nl.melonstudios.bmnw.weapon.missile.guidance.GuidanceComputer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomizableMissileEntity extends AbstractMissileEntity {
    public static final EntityDataAccessor<Boolean> BOOST_MODE_DATA =
            SynchedEntityData.defineId(CustomizableMissileEntity.class, EntityDataSerializers.BOOLEAN);
    public CustomizableMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public CustomizableMissileEntity(Level level, Vec3 pos, MissileBlueprint blueprint, int fuel) {
        this(BMNWEntityTypes.CUSTOMIZABLE_MISSILE.get(), level);
        this.setPos(pos);
        this.blueprint.takeInspiration(blueprint);

        this.hp = this.getMaxHealth();
        this.fuel = fuel;
    }
    public CustomizableMissileEntity(Level level, Vec3 pos, MissileBlueprint blueprint) {
        this(level, pos, blueprint, blueprint.fuselage.getFuelCapacity());
    }

    private final MissileBlueprint blueprint = MissileBlueprint.create();
    private final GuidanceComputer computer = GuidanceComputer.create(this);
    private int launchX, launchZ;
    private int targetX, targetZ;
    private int fuel = 0;
    public boolean boostMode = false;

    public MissileBlueprint getBlueprint() {
        return this.blueprint;
    }

    @Override
    protected void syncAdditionalValues(boolean clientSide) {
        if (clientSide) {
            this.boostMode = this.entityData.get(BOOST_MODE_DATA);
        } else {
            this.entityData.set(BOOST_MODE_DATA, this.boostMode);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOOST_MODE_DATA, this.boostMode);
    }

    @Override
    protected void spawnThrustParticles() {
        if (!this.blueprint.validationFlag || ((this.level().getGameTime() & 1) == 1)) return;
        Vec3 look = this.getLookAngle().normalize();
        Vec3 offset = look.scale(this.blueprint.calculateHalfHeight());
        Vec3 speed = look.scale(this.blueprint.thruster.getMaxPayload() * 0.2F);
        this.level().addParticle(BMNWParticleTypes.LARGE_MISSILE_SMOKE.get(),
                this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z,
                speed.x, speed.y, speed.z
        );
    }

    @Override
    public void tick() {
        super.tick();
        this.cachedRenderBB = null;
    }

    @Override
    protected void setDeltaMovementAccordingly() {
        this.computer.tick();
        this.fuel -= this.blueprint.thruster.getConsumption();
        Vec3 thrust = this.getLookAngle().scale(-1);
        this.setDeltaMovement(thrust);
    }
    @Override
    protected boolean shouldDetonate() {
        return this.onGround();
    }

    @Override
    protected boolean hasThrust() {
        return this.fuel > 0;
    }

    @Override
    protected int getMaxHealth() {
        return this.blueprint.calculateHealth();
    }

    @Override
    protected void writeNBT(CompoundTag nbt) {
        nbt.put("Blueprint", this.blueprint.serializeNBT());
        nbt.put("Computer", this.computer.serializeNBT());
        nbt.putInt("targetX", this.targetX);
        nbt.putInt("targetZ", this.targetZ);
        nbt.putInt("launchX", this.launchX);
        nbt.putInt("launchZ", this.launchZ);
        nbt.putInt("fuel", this.fuel);
    }
    @Override
    protected void readNBT(CompoundTag nbt) {
        this.blueprint.deserializeNBT(nbt.getCompound("Blueprint"));
        this.computer.deserializeNBT(nbt.getCompound("Computer"));
        this.targetX = nbt.getInt("targetX");
        this.targetZ = nbt.getInt("targetZ");
        this.launchX = nbt.getInt("launchX");
        this.launchZ = nbt.getInt("launchZ");
        this.fuel = nbt.getInt("fuel");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        this.blueprint.writeBuf(buf);
        this.computer.writeBuf(buf);
        buf.writeInt(this.targetX);
        buf.writeInt(this.targetZ);
        buf.writeInt(this.launchX);
        buf.writeInt(this.launchZ);
        buf.writeInt(this.fuel);
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        this.blueprint.readBuf(buf);
        this.computer.readBuf(buf);
        this.targetX = buf.readInt();
        this.targetZ = buf.readInt();
        this.launchX = buf.readInt();
        this.launchZ = buf.readInt();
        this.fuel = buf.readInt();
    }

    @Override
    protected void onImpact() {
        this.blueprint.warhead.getImpactHandler().onImpact(this, false);
    }

    @Override
    protected void onDamaged(DamageSource source, int dmg) {

    }
    @Override
    protected void onDestroyed(DamageSource source) {
        if (this.random.nextFloat() < this.blueprint.warhead.getDestructionDetonationChange()) {
            this.blueprint.warhead.getImpactHandler().onImpact(this, true);
        }
    }

    private AABB cachedRenderBB;
    @Override
    public AABB getBoundingBoxForCulling() {
        if (this.cachedRenderBB == null) {
            this.cachedRenderBB = this.getBoundingBox().inflate(this.blueprint.calculateHalfHeight());
        }
        return this.cachedRenderBB;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }
}
