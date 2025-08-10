package nl.melonstudios.bmnw.weapon.nuke.type;

import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class TsarBombaNukeType extends NukeType {
    @Override
    public int getBlastRadius() {
        return 512;
    }

    @Override
    public int getCharredTreesRadius() {
        return 640;
    }

    @Override
    public int getDestroyedLeavesRadius() {
        return 800;
    }

    @Override
    public float getReleasedRadiation() {
        return 2_000_000F;
    }

    @Override
    public int getReleasedRadiationLingerTicks() {
        return 25600;
    }

    @Override
    public Easing getReleasedRadiationDropOff() {
        return Easing.OUT_SINE;
    }

    @Override
    public int getFalloutRadius() {
        return 1280;
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
        return 45.0F;
    }

    @Override
    public int getSoundDistance() {
        return 4096;
    }

    public TsarBombaNukeType() {

    }
}
