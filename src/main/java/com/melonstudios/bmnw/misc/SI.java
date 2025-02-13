package com.melonstudios.bmnw.misc;

public class SI {
    public static float getKiloRedstoneWattHour(int rfTick) {
        return rfTick / 3_600_000.0f;
    }
}
