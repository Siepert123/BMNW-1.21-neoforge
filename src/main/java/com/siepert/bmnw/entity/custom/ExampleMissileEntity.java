package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ExampleMissileEntity extends MissileEntity {
    public ExampleMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onImpact() {
        NuclearChargeEntity entity = new NuclearChargeEntity(ModEntityTypes.NUCLEAR_CHARGE.get(), level());
        entity.setPos(this.position());
        level().addFreshEntity(entity);
    }
}
