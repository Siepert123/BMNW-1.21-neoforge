package nl.melonstudios.bmnw.weapon.nuke;

import nl.melonstudios.bmnw.misc.math.Easing;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class NukeType {
    public abstract int getBlastRadius();
    public abstract int getNuclearRemainsRadius();
    public abstract int getCharredTreesRadius();
    public abstract int getDestroyedLeavesRadius();
    public abstract float getReleasedRadiation();
    public abstract int getReleasedRadiationLingerTicks();
    public abstract Easing getReleasedRadiationDropOff();
    public abstract int getFalloutRadius();
    public abstract boolean hasDarkenedNuclearRemains();
    public abstract boolean hasShockwave();
    public abstract boolean isSoulType();
    public abstract float getMushroomCloudSize();
    public abstract int getSoundDistance();
    public int getEntityBlowRadius() {
        return this.getBlastRadius() * 4;
    }
    public int getEntityFireTicks() {
        return 200;
    }

    public @Nullable Consumer<FallingBombEntity> impactOverride() {
        return null;
    }
}
