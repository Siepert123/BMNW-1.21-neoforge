package nl.melonstudios.bmnw.weapon.explosion;

public enum ExplosionAlgorithm {
    UNOPTIMIZED_PACMAN(true);

    private final boolean warn;
    ExplosionAlgorithm(boolean warn) {
        this.warn = warn;
    }
    public boolean warn() {
        return this.warn;
    }

    @SafeVarargs
    public final <T> T pick(T... options) {
        return options[this.ordinal()];
    }
}
