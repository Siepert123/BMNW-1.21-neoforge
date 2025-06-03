package nl.melonstudios.bmnw.misc.math;

public enum Easing implements IEase {
    LINEAR {
        @Override
        public float ease(float x) {
            return x;
        }
    },
    IN_SINE {
        @Override
        public float ease(float x) {
            return (float)(1 - Math.cos((x * Math.PI) / 2));
        }
    },
    OUT_SINE {
        @Override
        public float ease(float x) {
            return (float)Math.sin((x * Math.PI) / 2);
        }
    },
    IN_OUT_SINE {
        @Override
        public float ease(float x) {
            return -(float)(Math.cos(Math.PI * x) - 1) / 2;
        }
    },
    IN_QUAD {
        @Override
        public float ease(float x) {
            return x * x;
        }
    },
    OUT_QUAD {
        @Override
        public float ease(float x) {
            return 1 - (1 - x) * (1 - x);
        }
    },
    IN_OUT_QUAD {
        @Override
        public float ease(float x) {
            return (float)(x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2);
        }
    },
    IN_CUBIC {
        @Override
        public float ease(float x) {
            return x * x * x;
        }
    },
    OUT_CUBIC {
        @Override
        public float ease(float x) {
            return (float)(1 - Math.pow(1 - x, 3));
        }
    },
    IN_OUT_CUBIC {
        @Override
        public float ease(float x) {
            return x < 0.5 ? 4 * x * x * x : (float)(1 - Math.pow(-2 * x + 2, 3) / 2);
        }
    },
    IN_QUART {
        @Override
        public float ease(float x) {
            return x * x * x * x;
        }
    },
    OUT_QUART {
        @Override
        public float ease(float x) {
            return (float)(1 - Math.pow(1 - x, 4));
        }
    },
    IN_OUT_QUART {
        @Override
        public float ease(float x) {
            return x < 0.5 ? 8 * x * x * x * x : (float)(1 - Math.pow(-2 * x + 2, 4) / 2);
        }
    },
    IN_QUINT {
        @Override
        public float ease(float x) {
            return x * x * x * x * x;
        }
    },
    OUT_QUINT {
        @Override
        public float ease(float x) {
            return (float)(1 - Math.pow(1 - x, 5));
        }
    },
    IN_OUT_QUINT {
        @Override
        public float ease(float x) {
            return x < 0.5 ? 16 * x * x * x * x * x : (float)(1 - Math.pow(-2 * x + 2, 5) / 2);
        }
    },
    IN_EXPO {
        @Override
        public float ease(float x) {
            return x == 0 ? 0 : (float)(Math.pow(2, 10 * x - 10));
        }
    },
    OUT_EXPO {
        @Override
        public float ease(float x) {
            return x == 1 ? 1 : (float)(1 - Math.pow(2, -10 * x));
        }
    },
    IN_OUT_EXPO {
        @Override
        public float ease(float x) {
            return x == 0
                    ? 0
                    : x == 1
                    ? 1
                    : x < 0.5 ? (float)Math.pow(2, 20 * x - 10) / 2
                    : (2 - (float)Math.pow(2, -20 * x + 10)) / 2;
        }
    },
    IN_CIRC {
        @Override
        public float ease(float x) {
            return (float)(1 - Math.sqrt(1 - Math.pow(x, 2)));
        }
    },
    OUT_CIRC {
        @Override
        public float ease(float x) {
            return (float)Math.sqrt(1 - Math.pow(x - 1, 2));
        }
    },
    IN_OUT_CIRC {
        @Override
        public float ease(float x) {
            return (float)(x < 0.5
                    ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                    : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);
        }
    },
    IN_BACK {
        @Override
        public float ease(float x) {
            float c1 = 1.70158F;
            float c3 = c1 + 1;

            return c3 * x * x * x - c1 * x * x;
        }
    },
    OUT_BACK {
        @Override
        public float ease(float x) {
            float c1 = 1.70158F;
            float c3 = c1 + 1;

            return (float)(1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
        }
    },
    IN_OUT_BACK {
        @Override
        public float ease(float x) {
            float c1 = 1.70158F;
            float c2 = c1 * 1.525F;

            return (float)(x < 0.5
                    ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                    : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
        }
    },
    IN_ELASTIC {
        @Override
        public float ease(float x) {
            float c4 = (float) ((2 * Math.PI) / 3);

            return x == 0
                    ? 0
                    : x == 1
                    ? 1
                    : (float)(-Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));
        }
    },
    OUT_ELASTIC {
        @Override
        public float ease(float x) {
            float c4 = (2 * (float)Math.PI) / 3;

            return x == 0
                    ? 0
                    : x == 1
                    ? 1
                    : (float)(Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
        }
    },
    IN_OUT_ELASTIC {
        @Override
        public float ease(float x) {
            float c5 = (2 * (float)Math.PI) / 4.5F;

            final float sin = (float)Math.sin((20 * x - 11.125) * c5);
            return x == 0
                    ? 0
                    : (float) (x == 1
                    ? 1
                    : x < 0.5
                    ? -(Math.pow(2, 20 * x - 10) * sin) / 2
                    : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1);
        }
    },
    IN_BOUNCE {
        @Override
        public float ease(float x) {
            return 1.0F - OUT_BOUNCE.ease(x);
        }
    },
    OUT_BOUNCE {
        @Override
        public float ease(float x) {
            float n1 = 7.5625F;
            float d1 = 2.75F;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5F / d1) * x + 0.75F;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25F / d1) * x + 0.9375F;
            } else {
                return n1 * (x -= 2.625F / d1) * x + 0.984375F;
            }
        }
    },
    IN_OUT_BOUNCE {
        @Override
        public float ease(float x) {
            return x < 0.5F ?
                    (1.0F - OUT_BOUNCE.ease(1 - 2 * x)) / 2.0F :
                    (1.0F + OUT_BOUNCE.ease(2 * x - 1)) / 2.0F;
        }
    }
}
