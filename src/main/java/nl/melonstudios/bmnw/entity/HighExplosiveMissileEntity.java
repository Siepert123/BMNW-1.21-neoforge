package nl.melonstudios.bmnw.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;

public class HighExplosiveMissileEntity extends MissileEntity {

    public HighExplosiveMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    protected HighExplosiveMissileEntity(Level level) {
        this(BMNWEntityTypes.HE_MISSILE.get(), level);
    }

    @Override
    protected void onImpact() {
        level().explode(this, getX(), getY(), getZ(), 16.0f, Level.ExplosionInteraction.MOB);
    }
}
