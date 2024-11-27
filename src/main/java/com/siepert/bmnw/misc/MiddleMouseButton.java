package com.siepert.bmnw.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
}
