package nl.melonstudios.bmnw.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import nl.melonstudios.bmnw.init.BMNWDamageSources;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractBulletEntity extends AbstractHurtingProjectile {
    protected float getDamage(@Nullable Entity target) {
        return 5.0f;
    }
    protected AbstractBulletEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onDeflection(@Nullable Entity entity, boolean deflectedByPlayer) {
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        result.getEntity().hurt(BMNWDamageSources.shot(result.getEntity().level()), getDamage(result.getEntity()));
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        discard();
    }
}
