package nl.melonstudios.bmnw.client;

import net.minecraft.world.level.Level;

public class BMNWClientBouncer {
    public static void addShockwave(Level level, long initTime, float x, float z, int max) {
        BMNWClient.addShockwave(level, initTime, x, z, max);
    }
}
