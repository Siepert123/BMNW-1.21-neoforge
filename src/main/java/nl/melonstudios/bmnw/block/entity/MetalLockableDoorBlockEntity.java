package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.math.Easing;
import nl.melonstudios.bmnw.wifi.PacketMetalLockableDoor;
import org.jetbrains.annotations.Nullable;

public class MetalLockableDoorBlockEntity extends BlockEntity implements ITickable {
    public static final int HANDLE_TURN_TICKS = 20;
    public static final int DOOR_TURN_TICKS = 60;
    public MetalLockableDoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.METAL_LOCKABLE_DOOR.get(), pos, blockState);
    }

    public int handleTicks = -1;
    public int doorTicks = -1;
    public int animationTicks = 0;
    public boolean open = this.getBlockState().getValue(BlockStateProperties.OPEN);

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void update() {
        if (this.animationTicks > 0) this.animationTicks--;
        if (this.open) {
            if (this.handleTicks != -1 && this.handleTicks < HANDLE_TURN_TICKS) {
                this.handleTicks++;
            } else if (this.doorTicks != -1 && this.doorTicks < DOOR_TURN_TICKS) {
                this.doorTicks++;
                if (this.doorTicks >= DOOR_TURN_TICKS) {
                    this.doorTicks = -1;
                    this.handleTicks = -1;
                }
            }
        } else {
            if (this.doorTicks != -1 && this.doorTicks < DOOR_TURN_TICKS) {
                this.doorTicks++;
            } else if (this.handleTicks != -1 && this.handleTicks < HANDLE_TURN_TICKS) {
                this.handleTicks++;
                if (this.handleTicks >= HANDLE_TURN_TICKS) {
                    this.handleTicks = -1;
                    this.doorTicks = -1;
                }
            }
        }
    }

    public boolean canSwitchState() {
        return this.animationTicks <= 0;
    }

    public boolean surpassedHalfDoorAnim() {
        return (this.doorTicks == -1 || this.doorTicks > (DOOR_TURN_TICKS / 2));
    }

    public boolean mayPass() {
        return this.open && this.doorTicks == -1;
    }

    public float getHandle(float pt) {
        if (this.handleTicks < 0) return this.open ? 1.0F : 0.0F;
        if (!this.open && this.handleTicks == 0 && this.doorTicks < DOOR_TURN_TICKS) return 1.0F;
        if (this.open && this.handleTicks >= HANDLE_TURN_TICKS) return 1.0F;
        float transition = this.handleTicks + pt;
        return Easing.IN_OUT_SINE.clampedEase(this.open ? transition / HANDLE_TURN_TICKS : 1.0F-(transition / HANDLE_TURN_TICKS));
    }
    public float getDoor(float pt) {
        if (this.doorTicks < 0) return this.open ? 1.0F: 0.0F;
        if (this.open && this.doorTicks == 0 && this.handleTicks < HANDLE_TURN_TICKS) return 0.0F;
        float transition = this.doorTicks + pt;
        return Easing.IN_OUT_QUAD.clampedEase(this.open ? transition / DOOR_TURN_TICKS : 1.0F - (transition / DOOR_TURN_TICKS));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCachedBB();
    }

    private AABB cachedBB = null;

    public AABB getCachedBB() {
        if (this.cachedBB == null) this.cachedBB = this.createCachedBB();
        return this.cachedBB;
    }

    public void invalidateCachedBB() {
        this.cachedBB = null;
    }

    private AABB createCachedBB() {
        BlockPos pos = this.worldPosition;
        return new AABB(
                pos.getX() - 0.5F, pos.getY() - 0.5F, pos.getZ() - 0.5F,
                pos.getX() + 1.5F, pos.getY() + 2.5F    , pos.getZ() + 1.5F
        );
    }

    public void setOpen(boolean open) {
        if (this.level instanceof ServerLevel level) {
            BlockPos pos = this.worldPosition;
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos),
                    new PacketMetalLockableDoor(pos.getX(), pos.getY(), pos.getZ(), open));
        }

        this.handleTicks = 0;
        this.doorTicks = 0;
        this.open = open;
        this.animationTicks = 80;
    }
}
