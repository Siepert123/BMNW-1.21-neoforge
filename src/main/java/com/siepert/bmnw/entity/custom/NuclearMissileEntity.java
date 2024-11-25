package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.entity.BMNWEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class NuclearMissileEntity extends MissileEntity {

    public NuclearMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    protected NuclearMissileEntity(Level level) {
        this(BMNWEntityTypes.NUCLEAR_MISSILE.get(), level);
    }

    @Override
    protected void onImpact() {
        NuclearChargeEntity entity = new NuclearChargeEntity(BMNWEntityTypes.NUCLEAR_CHARGE.get(), level());
        entity.setPos(this.position());
        level().addFreshEntity(entity);
    }
}
