package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;

public abstract class WireAttachedBlockEntity extends SyncedBlockEntity {
    public WireAttachedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract Collection<Vec3> wireConnectionsForRendering();

    public Vec3 getWireColor() {
        return Vec3.ZERO;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        Collection<Vec3> connections = this.wireConnectionsForRendering();
        BlockPos pos = this.getBlockPos();
        double minX = pos.getX();
        double minY = pos.getY();
        double minZ = pos.getZ();
        double maxX = pos.getX() + 1;
        double maxY = pos.getY() + 1;
        double maxZ = pos.getZ() + 1;

        for (Vec3 connect : connections) {
            minX = Math.min(minX, connect.x-0.1);
            minY = Math.min(minY, connect.y-0.1);
            minZ = Math.min(minZ, connect.z-0.1);
            maxX = Math.max(maxX, connect.x+0.1);
            maxY = Math.max(maxY, connect.y+0.1);
            maxZ = Math.max(maxZ, connect.z+0.1);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
