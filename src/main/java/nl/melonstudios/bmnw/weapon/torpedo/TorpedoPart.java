package nl.melonstudios.bmnw.weapon.torpedo;

public enum TorpedoPart {
    THRUSTER, FINS, HEAD;

    private final int index;
    TorpedoPart() {
        this.index = this.ordinal();
    }

    public int getIndex() {
        return this.index;
    }

    public static final TorpedoPart[] VALUES = {
            THRUSTER, FINS, HEAD,
    };
    public static TorpedoPart byIndexSafe(int index) {
        return VALUES[index % VALUES.length];
    }
}
