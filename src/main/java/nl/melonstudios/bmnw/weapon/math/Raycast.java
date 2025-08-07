package nl.melonstudios.bmnw.weapon.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Raycast {
    private final Vector3fc source;
    private final Vector3f delta = new Vector3f();
    private float x, y, z;
    private final BlockPos.MutableBlockPos currentBlockPos = new BlockPos.MutableBlockPos();

    public Raycast(Vector3fc source) {
        this.source = source;
    }
    public Raycast(float x, float y, float z) {
        this(new Vector3f(x, y, z));
    }
    public Raycast(Position position) {
        this(new Vector3f((float) position.x(), (float) position.y(), (float) position.z()));
    }
    public Raycast(double x, double y, double z) {
        this(new Vec3(x, y, z));
    }

    public void setDelta(float x, float y, float z) {
        this.delta.set(x, y, z).normalize(0.8F);
        this.x = this.source.x();
        this.y = this.source.y();
        this.z = this.source.z();
    }

    public boolean step() {
        this.x += this.delta.x;
        this.y += this.delta.y;
        this.z += this.delta.z;
        this.currentBlockPos.set(this.x, this.y, this.z);
        return true;
    }

    public BlockPos bp() {
        return this.currentBlockPos;
    }
}
