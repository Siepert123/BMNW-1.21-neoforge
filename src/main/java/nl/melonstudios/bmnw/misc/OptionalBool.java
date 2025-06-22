package nl.melonstudios.bmnw.misc;

import java.util.function.Supplier;

public enum OptionalBool {
    TRUE(Boolean.TRUE), FALSE(Boolean.FALSE), MAYBE(null);

    private final Boolean value;
    private final boolean isMaybe;
    OptionalBool(Boolean value) {
        this.value = value;
        this.isMaybe = value == null;
    }

    public boolean value(boolean ifMaybe) {
        return this.isMaybe() ? ifMaybe : this.value;
    }
    public boolean value(Supplier<Boolean> ifMaybe) {
        return this.isMaybe() ? ifMaybe.get() : this.value;
    }
    public boolean isMaybe() {
        return this.isMaybe;
    }
}
