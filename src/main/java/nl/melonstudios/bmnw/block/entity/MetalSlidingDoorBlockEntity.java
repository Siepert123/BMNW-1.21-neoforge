package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWStateProperties;
import nl.melonstudios.bmnw.interfaces.IOpenDoor;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.wifi.PacketSetOpenDoor;

public class MetalSlidingDoorBlockEntity extends BlockEntity implements ITickable, IOpenDoor {
    public MetalSlidingDoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.METAL_SLIDING_DOOR.get(), pos, blockState);
    }

    public boolean open = this.getBlockState().getValue(BMNWStateProperties.OPEN);
    public int doorTicks = -1;

    @Override
    public void update() {
        if (this.doorTicks != -1 && this.doorTicks < 20) {
            this.doorTicks++;
            if (this.doorTicks >= 20) {
                this.doorTicks = -1;
            }
        }
    }

    public boolean canSwitchState() {
        return this.doorTicks < 0;
    }
    public float getDoor(float pt) {
        if (this.doorTicks < 0) return this.open ? 1.0F : 0.0F;
        float prog = (this.doorTicks + pt) / 20.0F;
        return Easing.IN_OUT_QUAD.clampedEase(this.open ? prog : 1.0F-prog);
    }

    public void setOpen(boolean open) {
        if (this.level instanceof ServerLevel level) {
            BlockPos pos = this.worldPosition;
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), PacketSetOpenDoor.of(pos, open));
        }

        this.open = open;
        this.doorTicks = 0;
    }

    private AABB cachedBB = null;

    private AABB createCachedBB() {
        BlockPos pos = this.worldPosition;
        return new AABB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1
        );
    }

    public AABB getCachedBB() {
        if (this.cachedBB == null) this.cachedBB = this.createCachedBB();
        return this.cachedBB;
    }

    public void removeCachedBB() {
        this.cachedBB = null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.removeCachedBB();
    }
}
