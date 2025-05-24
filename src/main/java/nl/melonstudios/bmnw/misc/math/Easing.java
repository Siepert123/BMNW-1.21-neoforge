package nl.melonstudios.bmnw.misc.math;

import net.minecraft.util.Mth;

public enum Easing {
    LINEAR {
        @Override
        public float ease(float t) {
            return t;
        }
    },
    IN_SINE {
        @Override
        public float ease(float t) {
            return (float) (1 - Math.cos((t * Math.PI) / 2));
        }
    },
    OUT_SINE {
        @Override
        public float ease(float t) {
            return (float) Math.sin((t * Math.PI) / 2);
        }
    },
    IN_OUT_SINE {
        @Override
        public float ease(float t) {
            return (float) (-(Math.cos(Math.PI * t) - 1) / 2);
        }
    },
    IN_QUAD {
        @Override
        public float ease(float t) {
            return t * t;
        }
    },
    OUT_QUAD {
        @Override
        public float ease(float t) {
            return 1 - (1 - t) * (1 - t);
        }
    },
    //TODO: more easing functions
    ;

    public abstract float ease(float t);

    public float clampedEase(float t) {
        return Mth.clamp(this.ease(t), 0.0F, 1.0F);
    }
    public float easedLerp(float t, float start, float end) {
        return Mth.lerp(this.ease(t), start, end);
    }
    public float clampedEasedLerp(float t, float start, float end) {
        return Mth.clampedLerp(start, end, this.ease(t));
    }
}
