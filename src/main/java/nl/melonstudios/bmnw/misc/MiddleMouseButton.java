package nl.melonstudios.bmnw.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class MiddleMouseButton {
    private MiddleMouseButton() {

    }

    public static HitResult click(Entity from, double distanceMax, boolean incorporateLiquids) {
        return from.pick(distanceMax, 0, incorporateLiquids);
    }

    public static @Nullable BlockHitResult clickBlock(Entity from, double distanceMax, boolean incorporateLiquids) {
        HitResult resultOfEntitySimulatingMiddleMouseButtonClick = click(from, distanceMax, incorporateLiquids);
        if (resultOfEntitySimulatingMiddleMouseButtonClick.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return (BlockHitResult) resultOfEntitySimulatingMiddleMouseButtonClick;
    }
    public static @Nullable EntityHitResult clickEntity(Entity from, double distanceMax, boolean incorporateLiquids) {
        HitResult resultOfEntitySimulatingMiddleMouseButtonClick = click(from, distanceMax, incorporateLiquids);
        if (resultOfEntitySimulatingMiddleMouseButtonClick.getType() != HitResult.Type.ENTITY) {
            return null;
        }
        return (EntityHitResult) resultOfEntitySimulatingMiddleMouseButtonClick;
    }
    public static BlockHitResult clipBlocks(Level level, BlockPos source, BlockPos target,
                                            boolean incorporateLiquids, Entity entity) {
        return level.clip(new ClipContext(toVec3(source), toVec3(target),
                ClipContext.Block.OUTLINE,
                incorporateLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }

    public static Vec3 toVec3(Vec3i vec) {
        return new Vec3(vec.getX() + 0.5, vec.getY() + 0.5, vec.getZ() + 0.5);
    }
}
