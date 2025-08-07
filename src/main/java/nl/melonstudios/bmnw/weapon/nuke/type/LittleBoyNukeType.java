package nl.melonstudios.bmnw.weapon.nuke.type;

import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class LittleBoyNukeType extends NukeType {
    @Override
    public int getBlastRadius() {
        return 128;
    }

    @Override
    public int getNuclearRemainsRadius() {
        return 128;
    }

    @Override
    public int getCharredTreesRadius() {
        return 160;
    }

    @Override
    public int getDestroyedLeavesRadius() {
        return 320;
    }

    @Override
    public float getReleasedRadiation() {
        return 1_000_000F;
    }

    @Override
    public int getReleasedRadiationLingerTicks() {
        return 6400;
    }

    @Override
    public Easing getReleasedRadiationDropOff() {
        return Easing.OUT_SINE;
    }

    @Override
    public int getFalloutRadius() {
        return 320;
    }

    @Override
    public boolean hasDarkenedNuclearRemains() {
        return true;
    }

    @Override
    public boolean hasShockwave() {
        return true;
    }

    @Override
    public boolean isSoulType() {
        return false;
    }

    @Override
    public float getMushroomCloudSize() {
        return 15.0F;
    }

    @Override
    public int getSoundDistance() {
        return 1024;
    }

    public LittleBoyNukeType() {

    }
}
