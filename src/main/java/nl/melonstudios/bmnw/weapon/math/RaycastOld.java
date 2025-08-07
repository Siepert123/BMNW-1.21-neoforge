package nl.melonstudios.bmnw.weapon.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RaycastOld {
    private static final Logger LOGGER = LogManager.getLogger(RaycastOld.class);
    private final Vector3fc source;
    private final Vector3f delta;
    private float x, y, z;
    private final BlockPos.MutableBlockPos currentBlockPos = new BlockPos.MutableBlockPos();

    public RaycastOld(Vector3fc source) {
        this.source = source;
        this.delta = new Vector3f();
    }
    public RaycastOld(Position source) {
        this(new Vector3f((float) source.x(), (float) source.y(), (float) source.z()));
    }
    public RaycastOld(float x, float y, float z) {
        this(new Vector3f(x, y, z));
    }

    public void reset(float dx, float dy, float dz) {
        this.delta.set(dx, dy, dz).normalize();
        this.x = this.source.x();
        this.y = this.source.y();
        this.z = this.source.z();
    }

    public boolean step() {
        float xDist = this.getXDistance();
        float yDist = this.getYDistance();
        float zDist = this.getZDistance();
        float step = this.getLowest(xDist, yDist, zDist);
        if (Float.isNaN(step)) return false;
        this.x += step * this.delta.x;
        this.y += step * this.delta.y;
        this.z += step * this.delta.z;
        return true;
    }

    private float getLowest(float... candidates) {
        float lowest = Float.NaN;
        for (float candidate : candidates) {
            if (Float.isNaN(candidate)) {
                continue;
            }
            if (Float.isNaN(lowest)) {
                lowest = candidate;
                continue;
            }
            if (candidate < lowest) {
                lowest = candidate;
            }
        }
        return lowest;
    }

    private float getXDistance() {
        if (this.delta.x == 0.0F) return Float.NaN;
        float nextX = Mth.ceil(Math.nextUp(this.x));
        return (nextX - this.x) / this.delta.x;
    }
    private float getYDistance() {
        if (this.delta.y == 0.0F) return Float.NaN;
        float nextY = Mth.ceil(Math.nextUp(this.y));
        return (nextY - this.y) / this.delta.y;
    }
    private float getZDistance() {
        if (this.delta.z == 0.0F) return Float.NaN;
        float nextZ = Mth.ceil(Math.nextUp(this.z));
        return (nextZ - this.z) / this.delta.z;
    }

    public BlockPos.MutableBlockPos getCurrentBlockPos() {
        this.currentBlockPos.set(this.x, this.y, this.z);
        LOGGER.debug(this.currentBlockPos);
        return this.currentBlockPos;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
