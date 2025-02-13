package com.melonstudios.bmnw.item.custom;

import com.melonstudios.bmnw.entity.custom.AbstractBulletEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

import java.lang.reflect.Constructor;

public class BulletItem extends Item implements ProjectileItem {
    private final Class<? extends AbstractBulletEntity> bulletClass;
    public BulletItem(Properties properties, Class<? extends AbstractBulletEntity> bulletClass) {
        super(properties);
        this.bulletClass = bulletClass;
    }

    @Override
    public AbstractBulletEntity asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        try {
            Constructor<? extends AbstractBulletEntity> constructor = bulletClass.getConstructor(Level.class);
            constructor.setAccessible(true);
            AbstractBulletEntity bullet = constructor.newInstance(level);
            bullet.setPos(pos.x(), pos.y(), pos.z());
            return bullet;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
