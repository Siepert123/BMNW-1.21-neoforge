package nl.melonstudios.bmnw.misc;

import net.minecraft.core.Direction;

public class SI {
    public static float getKiloRedstoneWattHour(int rfTick) {
        return rfTick / 3_600_000.0f;
    }

    public static final Direction[] DIRECTIONS_WITH_NULL = {
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
            null
    };

    public static final float PX = 0.0625f;
}
