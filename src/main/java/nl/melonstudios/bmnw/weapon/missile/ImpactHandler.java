package nl.melonstudios.bmnw.weapon.missile;

import nl.melonstudios.bmnw.weapon.missile.entity.AbstractMissileEntity;

@FunctionalInterface
public interface ImpactHandler {
    void onImpact(AbstractMissileEntity missile, boolean causedByDestruction);

    default ImpactHandler andThen(ImpactHandler other) {
        return (missile, causedByDestruction) -> {
            this.onImpact(missile, causedByDestruction);
            other.onImpact(missile, causedByDestruction);
        };
    }
}
