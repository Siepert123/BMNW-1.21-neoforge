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

    public static final float[] BRIGHTNESS_BY_AXIS = {
            0.85F, /* X */ 1.0F, /* Y */ 0.92F, /* Z */
    };
    public static float brightnessByDirection(Direction d) {
        return BRIGHTNESS_BY_AXIS[d.getAxis().ordinal()];
    }

    public static final float PX = 0.0625f;
}
