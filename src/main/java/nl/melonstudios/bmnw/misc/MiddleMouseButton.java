package nl.melonstudios.bmnw.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;

public class MiddleMouseButton {
    private MiddleMouseButton() {

    }

    public static HitResult click(Entity from, double distanceMax, boolean incorporateLiquids) {
        return pick(from, distanceMax, distanceMax, incorporateLiquids);
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

    //From game renderer
    public static HitResult pick(Entity entity, double blockInteractionRange, double entityInteractionRange, boolean incorporateLiquids) {
        double d0 = Math.max(blockInteractionRange, entityInteractionRange);
        double d1 = Mth.square(d0);
        Vec3 vec3 = entity.getEyePosition();
        HitResult hitresult = entity.pick(d0, 1, incorporateLiquids);
        double d2 = hitresult.getLocation().distanceToSqr(vec3);
        if (hitresult.getType() != HitResult.Type.MISS) {
            d1 = d2;
            d0 = Math.sqrt(d2);
        }

        Vec3 vec31 = entity.getViewVector(1);
        Vec3 vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
        float f = 1.0F;
        AABB aabb = entity.getBoundingBox().expandTowards(vec31.scale(d0)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(
                entity, vec3, vec32, aabb, p_234237_ -> !p_234237_.isSpectator(), d1
        );
        return entityhitresult != null && entityhitresult.getLocation().distanceToSqr(vec3) < d2
                ? filterHitResult(entityhitresult, vec3, entityInteractionRange)
                : filterHitResult(hitresult, vec3, blockInteractionRange);
    }

    private static HitResult filterHitResult(HitResult hitResult, Vec3 pos, double blockInteractionRange) {
        Vec3 vec3 = hitResult.getLocation();
        if (!vec3.closerThan(pos, blockInteractionRange)) {
            Vec3 vec31 = hitResult.getLocation();
            Direction direction = Direction.getNearest(vec31.x - pos.x, vec31.y - pos.y, vec31.z - pos.z);
            return BlockHitResult.miss(vec31, direction, BlockPos.containing(vec31));
        } else {
            return hitResult;
        }
    }
}
