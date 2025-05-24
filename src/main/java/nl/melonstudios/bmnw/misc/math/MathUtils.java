package nl.melonstudios.bmnw.misc.math;

import net.minecraft.core.Direction;

public class MathUtils {
    public static double getRelative(Direction.Axis axis, double yaw, double pitch, float radius) {
        return switch (axis) {
            case X -> Math.sin(Math.toRadians(yaw)) * radius * Math.sin(Math.toRadians(pitch));
            case Y -> Math.cos(Math.toRadians(pitch)) * radius;
            case Z -> Math.cos(Math.toRadians(yaw)) * radius * Math.sin(Math.toRadians(pitch));
        };
    }
}
