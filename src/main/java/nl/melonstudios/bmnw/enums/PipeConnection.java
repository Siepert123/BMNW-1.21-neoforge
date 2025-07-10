package nl.melonstudios.bmnw.enums;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;
import java.util.Set;

public enum PipeConnection implements StringRepresentable {
    FALSE(false, false, "false"),
    TRUE(true, false, "true"),
    FORCED_FALSE(false, true, "forced_false");

    private final boolean isConnected;
    private final boolean isForcedOff;
    private final String name;

    PipeConnection(boolean isConnected, boolean isForcedOff, String name) {
        if (isConnected && isForcedOff) throw new IllegalArgumentException("h");
        this.isConnected = isConnected;
        this.isForcedOff = isForcedOff;
        this.name = name;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
    public boolean isForcedOff() {
        return this.isForcedOff;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }

    public static PipeConnection fromBoolean(boolean b) {
        return b ? PipeConnection.TRUE : PipeConnection.FALSE;
    }

    public static final Set<PipeConnection> VALUES_SET = Set.of(FALSE, TRUE, FORCED_FALSE);
}
