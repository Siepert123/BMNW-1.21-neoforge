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
    IN_OUT_QUAD {
        @Override
        public float ease(float t) {
            return t < 0.5F ? 2 * t * t : 1 - (float)Math.pow(-2 * t + 2, 2) / 2;
        }
    },
    IN_CUBIC {
        @Override
        public float ease(float t) {
            return t * t * t;
        }
    },
    OUT_CUBIC {
        @Override
        public float ease(float t) {
            return (float) (1 - Math.pow(1 - t, 3));
        }
    },
    IN_OUT_CUBIC {
        @Override
        public float ease(float t) {
            return t < 0.5F ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
        }
    }
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
