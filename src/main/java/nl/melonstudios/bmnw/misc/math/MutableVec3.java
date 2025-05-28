package nl.melonstudios.bmnw.misc.math;

public class MutableVec3 {
    public double x, y, z;

    public MutableVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public MutableVec3() {
        this(0.0, 0.0, 0.0);
    }

    public MutableVec3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
}
