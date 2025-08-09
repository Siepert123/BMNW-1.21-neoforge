package nl.melonstudios.bmnw.weapon.nuke.type;

import net.minecraft.sounds.SoundEvent;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class DroppedSoulfireBombNukeType extends NukeType {
    @Override
    public int getBlastRadius() {
        return 64;
    }

    @Override
    public int getNuclearRemainsRadius() {
        return 0;
    }

    @Override
    public int getCharredTreesRadius() {
        return 0;
    }

    @Override
    public int getDestroyedLeavesRadius() {
        return 0;
    }

    @Override
    public float getReleasedRadiation() {
        return 0;
    }

    @Override
    public int getReleasedRadiationLingerTicks() {
        return 0;
    }

    @Override
    public Easing getReleasedRadiationDropOff() {
        return Easing.LINEAR;
    }

    @Override
    public int getFalloutRadius() {
        return 0;
    }

    @Override
    public boolean hasDarkenedNuclearRemains() {
        return false;
    }

    @Override
    public boolean hasShockwave() {
        return false;
    }

    @Override
    public boolean isSoulType() {
        return true;
    }

    @Override
    public float getMushroomCloudSize() {
        return 5;
    }

    @Override
    public int getSoundDistance() {
        return 512;
    }

    @Override
    public int getMinimalBiomeRadius() {
        return 0;
    }

    @Override
    public int getNormalBiomeRadius() {
        return 0;
    }

    @Override
    public int getSevereBiomeRadius() {
        return 0;
    }

    @Override
    public SoundEvent getExplosionSound() {
        return BMNWSounds.LARGE_EXPLOSION.get();
    }

    public DroppedSoulfireBombNukeType() {

    }
}
