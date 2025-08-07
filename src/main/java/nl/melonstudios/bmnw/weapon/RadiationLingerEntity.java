package nl.melonstudios.bmnw.weapon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class RadiationLingerEntity extends Entity {
    public RadiationLingerEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public RadiationLingerEntity(Level level, Vec3 pos, float releasedRadiation, int lingerTicks, Easing dropoff) {
        this(BMNWEntityTypes.RADIATION_LINGER.get(), level);
        this.setPos(pos);
        this.releasedRadiation = releasedRadiation;
        this.lingerTicks = lingerTicks;
        this.dropoff = dropoff;
    }
    public RadiationLingerEntity(Level level, Vec3 pos, NukeType nukeType) {
        this(level, pos, nukeType.getReleasedRadiation(), nukeType.getReleasedRadiationLingerTicks(), nukeType.getReleasedRadiationDropOff());
    }

    private float releasedRadiation;
    private int lingerTicks;
    private Easing dropoff;

    private int age;

    @Override
    public void tick() {
        if (this.level().isClientSide) return;
        if (++this.age > this.lingerTicks) {
            this.discard();
            return;
        }

        float delta = (float) this.age / this.lingerTicks;
        float released = this.dropoff.clampedEasedLerp(this.releasedRadiation, 0.0F, delta);

        ChunkRadiationManager.handler.increaseRadiation(this.level(), this.blockPosition(), released);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.releasedRadiation = compound.getFloat("rads");
        this.lingerTicks = compound.getInt("linger");
        this.dropoff = Easing.valueOf(compound.getString("dropoff"));
        this.age = compound.getInt("age");
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("rads", this.releasedRadiation);
        compound.putInt("linger", this.lingerTicks);
        compound.putString("dropoff", this.dropoff.toString());
        compound.putInt("age", this.age);
    }
}
