package nl.melonstudios.bmnw.misc;

public enum OptionalBool {
    TRUE(Boolean.TRUE), FALSE(Boolean.FALSE), MAYBE(null);

    private final Boolean value;
    OptionalBool(Boolean value) {
        this.value = value;
    }

    public boolean value(boolean ifMaybe) {
        return this.value == null ? ifMaybe : this.value;
    }
}
