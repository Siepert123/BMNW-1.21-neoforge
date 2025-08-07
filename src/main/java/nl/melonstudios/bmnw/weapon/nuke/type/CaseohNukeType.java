package nl.melonstudios.bmnw.weapon.nuke.type;

import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class CaseohNukeType extends NukeType {
    @Override
    public int getBlastRadius() {
        return 256;
    }

    @Override
    public int getNuclearRemainsRadius() {
        return 256;
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
        return 12800;
    }

    @Override
    public Easing getReleasedRadiationDropOff() {
        return Easing.OUT_SINE;
    }

    @Override
    public int getFalloutRadius() {
        return 640;
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
        return 25.0F;
    }

    @Override
    public int getSoundDistance() {
        return 2048;
    }

    public CaseohNukeType() {

    }
}
