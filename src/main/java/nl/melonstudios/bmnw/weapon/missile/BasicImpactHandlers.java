package nl.melonstudios.bmnw.weapon.missile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.weapon.missile.entity.AbstractMissileEntity;

import java.util.function.Function;

@SuppressWarnings("unused")
public class BasicImpactHandlers {
    public static void empty(AbstractMissileEntity missile, boolean causedByDestruction) {}
    public static ImpactHandler explode(final float strength, final boolean fire,
                                                            final Level.ExplosionInteraction interaction) {
        return (missile, causedByDestruction) -> {
            Level level = missile.level();
            if (!level.isClientSide) {
                level.explode(missile, missile.getX(), missile.getY(), missile.getZ(), strength, fire, interaction);
            }
        };
    }
    public static ImpactHandler spawnEntity(final Function<Level, Entity> entity) {
        return (missile, causedByDestruction) -> {
            Level level = missile.level();
            if (!level.isClientSide) {
                level.addFreshEntity(entity.apply(level));
            }
        };
    }
}
