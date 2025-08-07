package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.weapon.math.Raycast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ThreadedExplosionHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static int nextThreadID = 0;

    private final Level level;
    private final Vec3 pos;
    private final ExplosionProps params;
    private final double explosionStr;
    public ThreadedExplosionHandler(Level level, Vec3 pos, ExplosionProps params, double explosionStrength) {
        this.level = level;
        this.pos = pos;
        this.params = params;
        this.explosionStr = explosionStrength;

        this.raycast = new Raycast(this.pos);
        this.bound = Mth.ceil(this.explosionStr*2);
        this.bound2 = this.bound * this.bound;
        this.rx = -this.bound;
        this.ry = -this.bound;
        this.rz = -this.bound;
    }

    public static final ArrayList<ThreadedExplosionHandler> ACTIVE_HANDLERS = new ArrayList<>();

    private boolean canBeRemoved = false;
    public boolean isCanBeRemoved() {
        return this.canBeRemoved;
    }

    private final Raycast raycast;
    private final int bound;
    private final int bound2;
    private int rx, ry, rz;
    private boolean isDoneRaycasting = false;
    private final ArrayList<BlockPos> affectedPositions = new ArrayList<>();
    private final BlockPos.MutableBlockPos last = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos next = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos misc = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos misc2 = new BlockPos.MutableBlockPos();

    public void update(long msBudget) {
        if (this.canBeRemoved) return;
        this.level.getProfiler().push("Nuke calculations");
        long stop = System.currentTimeMillis() + msBudget;
        double str;
        while (System.currentTimeMillis() < stop) {
            if (this.isDoneRaycasting) {
                if (this.affectedPositions.isEmpty()) this.canBeRemoved = true;
                else {
                    BlockPos pos = this.affectedPositions.removeFirst();
                    this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            } else {
                if (Mth.ceil(Math.sqrt(this.rx*this.rx+this.ry*this.ry+this.rz*this.rz)) == this.bound) {
                    str = this.explosionStr;
                    this.raycast.setDelta(this.rx, this.ry, this.rz);
                    while (str > 0.0) {
                        if (!this.last.equals(this.next)) {
                            str -= this.getBlastRes(this.level, this.next);
                            if (str > 0.0 && !this.affectedPositions.contains(this.next)) {
                                this.affectedPositions.add(this.next.immutable());
                            }
                        }
                        this.raycast.step();
                        this.last.set(this.next);
                        this.next.set(this.raycast.bp());
                    }
                }
                this.rx++;
                if (this.rx > this.bound) {
                    this.rx = -this.bound;
                    this.ry++;
                    if (this.ry > this.bound) {
                        this.ry = -this.bound;
                        this.rz++;
                        if (this.rz > this.bound) {
                            this.rz = -this.bound;
                            this.isDoneRaycasting = true;
                        }
                    }
                }
            }
        }
        this.level.getProfiler().pop();
    }

    private float getBlastRes(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return 0.5F;
        if (state.getBlock() instanceof LiquidBlock) return 0.5F;
        if (state.is(Blocks.OBSIDIAN)) return Blocks.STONE.getExplosionResistance() * 3;
        return Math.max(state.getExplosionResistance(level, pos, null), 1.0F);
    }

    public static Vec3 calculateViewVector(double xRot, double yRot) {
        double f = xRot * (float) (Math.PI / 180.0);
        double f1 = -yRot * (float) (Math.PI / 180.0);
        double f2 = Math.cos(f1);
        double f3 = Math.sin(f1);
        double f4 = Math.cos(f);
        double f5 = Math.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
