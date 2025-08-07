package nl.melonstudios.bmnw.weapon.nuke;

import nl.melonstudios.bmnw.misc.math.Easing;

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
}
