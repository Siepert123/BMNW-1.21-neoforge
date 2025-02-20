package nl.melonstudios.bmnw.entity;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWConfig;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AntiMissileMissileEntity extends MissileEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    public AntiMissileMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 2048, 0));
    }
    protected AntiMissileMissileEntity(Level level) {
        this(BMNWEntityTypes.ANTI_MISSILE_MISSILE.get(), level);
    }

    private MissileEntity target = null;

    private void findTarget() {
        List<MissileEntity> missiles = level().getEntitiesOfClass(MissileEntity.class, new AABB(
                position().add(-64, 16, -64),
                position().add(64, 512, 64)
        ));

        MissileEntity closestMissile = null;
        float closestDistance = Float.MAX_VALUE;
        for (MissileEntity missile : missiles) {
            if (missile.distanceTo(this) < closestDistance && !missile.isRemoved()) {
                closestMissile = missile;
                closestDistance = missile.distanceTo(this);
            }
        }

        target = closestMissile;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("targetUUID")) {
            try {
                target = (MissileEntity) level().getEntity(compound.getInt("targetUUID"));
            } catch (ClassCastException e) {
                LOGGER.fatal("Entity of ID {} wasn't a MissileEntity!", compound.get("targetUUID"));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (target != null) {
            compound.putInt("targetUUID", target.getId());
        }
    }

    Vec3 prevpos = Vec3.ZERO;

    @Override
    public void baseTick() {
        prevpos = position();
        createSmoke();
        if (target == null || target.isRemoved()) {
            target = null;

            this.moveRelative(3, getLookAngle());

            findTarget();
        } else {
            this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());

            this.moveRelative(3, getLookAngle());

            if (this.distanceTo(target) < 3) {
                if (shouldImpact(BMNWConfig.antiMissileImpactChance)) target.onImpact();
                target.kill();
                this.kill();
            }
        }
        if (prevpos.equals(position()) || position().y() > 512 || position().y() < -128) this.kill();
    }

    private boolean shouldImpact(float chance) {
        if (Float.compare(chance, 0) <= 0) return false;
        if (Float.compare(chance, 1) >= 0) return true;
        return random.nextFloat() < chance;
    }

    @Override
    protected void onImpact() {

    }

    @Override
    public void moveRelative(float amount, Vec3 relative) {
        Vec3 vec3 = getInputVector(relative, amount, this.getYRot());
        this.setPos(this.position().add(vec3));
    }
    private static Vec3 getInputVector(Vec3 relative, float motionScaler, float facing) {
        double d0 = relative.lengthSqr();
        if (d0 < 1.0E-7) {
            return Vec3.ZERO;
        } else {
            Vec3 vec3 = (d0 > 1.0 ? relative.normalize() : relative).scale((double)motionScaler);
            float f = Mth.sin(facing * (float) (Math.PI / 180.0));
            float f1 = Mth.cos(facing * (float) (Math.PI / 180.0));
            return new Vec3(vec3.x * (double)f1 - vec3.z * (double)f, vec3.y, vec3.z * (double)f1 + vec3.x * (double)f);
        }
    }
}
