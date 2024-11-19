package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ExampleMissileEntity extends MissileEntity {
    public ExampleMissileEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    final boolean b = true;
    @Override
    protected void onImpact() {
        if (b) {
            level().explode(this, getX(), getY(), getZ(), 4.0f, Level.ExplosionInteraction.MOB);
            for (int i = 0; i < 50; i++) {
                BlockDebrisEntity entity = new BlockDebrisEntity(ModEntityTypes.BLOCK_DEBRIS.get(), level());
                entity.setPos(this.position().add(0, 1, 0));
                entity.setDeltaMovement((random.nextDouble() - random.nextDouble())*5,
                        (random.nextDouble() - random.nextDouble())*5,
                        (random.nextDouble() - random.nextDouble())*5);
                entity.setDebrisState(Blocks.BRICKS.defaultBlockState());
                level().addFreshEntity(entity);
            }
        } else {
            NuclearChargeEntity entity = new NuclearChargeEntity(ModEntityTypes.NUCLEAR_CHARGE.get(), level());
            entity.setPos(this.position());
            level().addFreshEntity(entity);
        }
    }
}
