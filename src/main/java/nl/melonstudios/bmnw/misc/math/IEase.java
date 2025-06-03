package nl.melonstudios.bmnw.misc.math;

import net.minecraft.util.Mth;

@FunctionalInterface
public interface IEase {
    /**
     * @param x The absolute progress defined by [0,1]
     * @return The eased version of the progress
     */
    float ease(float x);

    default float clampedEase(float x) {
        return Mth.clamp(this.ease(x), 0.0F, 1.0F);
    }
    default float easedLerp(float delta, float start, float end) {
        return Mth.lerp(this.ease(delta), start, end);
    }
    default float clampedEasedLerp(float a, float b, float x) {
        return Mth.clampedLerp(a, b, this.ease(x));
    }
}
