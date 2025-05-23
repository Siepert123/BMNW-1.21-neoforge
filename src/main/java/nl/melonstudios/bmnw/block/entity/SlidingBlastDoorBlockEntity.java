package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.block.doors.SlidingBlastDoorBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.wifi.PacketSlidingBlastDoor;
import org.jetbrains.annotations.Nullable;

public class SlidingBlastDoorBlockEntity extends BlockEntity implements ITickable {
    public static final int DOOR_TRANSITION_TICKS = 40;
    public static final int DOOR_UNSCREW_TICKS = 65;
    public SlidingBlastDoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.SLIDING_BLAST_DOOR.get(), pos, blockState);
    }

    public boolean lastShowScrews = false;
    public boolean showScrews = false;

    public int unscrewTicks = -1;
    public int transitionTicks = -1;
    public int animationTicks = 0;
    public boolean open = this.getBlockState().getValue(SlidingBlastDoorBlock.OPEN);

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void sync() {
        if (this.level instanceof ServerLevel serverLevel) serverLevel.getChunkSource().blockChanged(this.worldPosition);
    }

    @Override
    public void update() {
        this.lastShowScrews = this.showScrews;
        if (this.animationTicks > 0) this.animationTicks--;
        if (this.unscrewTicks != -1 && this.unscrewTicks < DOOR_UNSCREW_TICKS && this.open) {
            this.unscrewTicks++;
        } else if (this.transitionTicks != -1 && this.transitionTicks < DOOR_TRANSITION_TICKS) {
            this.transitionTicks++;
            if (this.transitionTicks >= DOOR_TRANSITION_TICKS) {
                this.transitionTicks = -1;
                this.unscrewTicks = -1;
            }
        }
        this.showScrews = this.showScrews();
    }

    public boolean canSwitchState() {
        if (this.level == null) return false;
        return this.animationTicks <= 0 &&
                !(this.level.hasNeighborSignal(this.worldPosition) || this.level.hasNeighborSignal(this.worldPosition.above()));
    }

    public boolean mayPass() {
        return this.open && this.unscrewTicks == -1;
    }

    public float getSlide(float pt) {
        if (transitionTicks < 0) return this.open ? 1.0F : 0.0F;
        if (this.transitionTicks == 0 && this.unscrewTicks < DOOR_UNSCREW_TICKS && this.open) return 0.0F;
        float transition = this.transitionTicks + pt;
        return this.open ? transition / DOOR_TRANSITION_TICKS : 1.0F-(transition / DOOR_TRANSITION_TICKS);
    }
    public boolean showScrews() {
        if (this.open) {
            return this.unscrewTicks > 0 && this.unscrewTicks < DOOR_UNSCREW_TICKS;
        } else {
            return this.animationTicks <= 0;
        }
    }
    // 0.0F to 1.0F
    public float getScrewRot(float pt) {
        if (this.unscrewTicks < 0 || !this.open) return 0.0F;
        float unscrew = this.unscrewTicks + pt;
        return Mth.clamp(unscrew / DOOR_UNSCREW_TICKS, 0.0F, 1.0F);
    }

    public float getScrewExpansion(float pt) {
        if (this.showScrews == this.lastShowScrews) return this.showScrews ? 0.0F : 0.125F;
        return this.showScrews ? Mth.clampedLerp(0.125F, 0.0F, pt) : Mth.clampedLerp(0.0F, 0.125F, pt);
    }

    @Override
    public void setRemoved() {
        this.invalidateCachedBB();
        super.setRemoved();
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
                        new PacketSlidingBlastDoor(pos.getX(), pos.getY(), pos.getZ(), open));
        }
        this.unscrewTicks = 0;
        this.transitionTicks = 0;
        this.open = open;
        this.animationTicks = open ? 200 : 70;
    }
}
