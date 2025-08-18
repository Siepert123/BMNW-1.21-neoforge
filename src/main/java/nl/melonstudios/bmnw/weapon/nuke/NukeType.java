package nl.melonstudios.bmnw.weapon.nuke;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.misc.math.Easing;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class NukeType {
    public abstract int getBlastRadius();
    public float getBlastStrength() {
        return this.getBlastRadius() * 2;
    }
    public int getNuclearRemainsRadius() {
        return this.getBlastRadius();
    }
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
    public float entityDamageMultiplier() {
        return 1.0F;
    }

    public @Nullable Consumer<FallingBombEntity> impactOverride() {
        return null;
    }

    public SoundEvent getExplosionSound() {
        return BMNWSounds.NUCLEAR_BOOM.get();
    }

    public int getMinimalBiomeRadius() {
        return Mth.ceil(this.getBlastRadius() * 2.0F);
    }
    public int getNormalBiomeRadius() {
        return this.getBlastRadius();
    }
    public int getSevereBiomeRadius() {
        return Mth.ceil(this.getBlastRadius() * 0.5F);
    }
    public boolean brightensSky() {
        return true;
    }
    public double lightBeamMultiplier() {
        return this.getBlastRadius();
    }

    public boolean grantsAchievement() {
        return true;
    }
}
