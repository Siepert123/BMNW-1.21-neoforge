package nl.melonstudios.bmnw.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Collections;
import java.util.List;

public class Library {
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

    public static boolean isObstructed(Level level, double x, double y, double z, double a, double b, double c) {
        return isObstructed(level, new Vec3(x, y, z), new Vec3(a, b, c));
    }
    public static boolean isObstructed(Level level, Vec3 a, Vec3 b) {
        BlockHitResult result = level.clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
        return result.getType() == HitResult.Type.MISS;
    }



    public static final List<Direction> SURROUND_X =
            ImmutableList.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
    public static final List<Direction> SURROUND_Y =
            ImmutableList.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    public static final List<Direction> SURROUND_Z =
            ImmutableList.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
    public static List<Direction> getSurrounding(Direction.Axis axis) {
        return switch (axis) {
            case X -> SURROUND_X;
            case Y -> SURROUND_Y;
            case Z -> SURROUND_Z;
        };
    }

    public static Direction.Axis toggleAxis(Direction.Axis toggle, Direction.Axis around) {
        return switch (around) {
            case X -> switch (toggle) {
                case X -> Direction.Axis.X;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.Y;
            };
            case Y -> switch (toggle) {
                case X -> Direction.Axis.Z;
                case Y -> Direction.Axis.Y;
                case Z -> Direction.Axis.X;
            };
            case Z -> switch (toggle) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.X;
                case Z -> Direction.Axis.Z;
            };
        };
    }

    public static Direction determineBlockPartition(Vec3 clickLocation) {
        Direction d = Direction.getNearest(clickLocation.multiply(1, 0, 1));
        if (d.getAxis() == Direction.Axis.Y) return null;
        return d;
    }
}
