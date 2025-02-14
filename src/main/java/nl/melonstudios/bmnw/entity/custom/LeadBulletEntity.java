package nl.melonstudios.bmnw.entity.custom;

import nl.melonstudios.bmnw.entity.BMNWEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LeadBulletEntity extends AbstractBulletEntity  {
    public LeadBulletEntity(EntityType<LeadBulletEntity> entityType, Level level) {
        super(entityType, level);
    }
    protected LeadBulletEntity(Level level) {
        this(BMNWEntityTypes.LEAD_BULLET.get(), level);
    }
}
