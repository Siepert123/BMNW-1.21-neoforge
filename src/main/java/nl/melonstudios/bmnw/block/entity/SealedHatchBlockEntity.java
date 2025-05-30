package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.block.doors.SealedHatchBlock;
import nl.melonstudios.bmnw.cfg.BMNWClientConfig;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.RandomHelper;
import nl.melonstudios.bmnw.wifi.PacketSealedHatch;
import org.jetbrains.annotations.Nullable;

public class SealedHatchBlockEntity extends BlockEntity implements ITickable {
    public static final int HATCH_VALVE_OPEN_TICKS = 50;
    public static final int HATCH_TURN_OPEN_TICKS = 27;
    public static final int HATCH_TURN_CLOSE_TICKS = 23;
    public static final int HATCH_VALVE_CLOSE_TICKS = 62;
    public SealedHatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.SEALED_HATCH.get(), pos, blockState);

        this.valveOffset = RandomHelper.nextInt(pos.asLong(), 2, 360);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.valveOffset = RandomHelper.nextInt(this.worldPosition.asLong(), 2, 360);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateBB();
    }

    public int valveTicks = -1;
    public int turnTicks = -1;
    public int animationTicks = 0;
    public boolean open = this.getBlockState().getValue(SealedHatchBlock.OPEN);
    private float valveOffset;

    @OnlyIn(Dist.CLIENT)
    public float getValveOffset() {
        return BMNWClientConfig.enableRandomRotationOffsets ? this.valveOffset : 0.0F;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void update() {
        if (this.animationTicks > 0) this.animationTicks--;

        if (this.open) {
            if (this.valveTicks != -1 && this.valveTicks < HATCH_VALVE_OPEN_TICKS) {
                this.valveTicks++;
            } else if (this.turnTicks != -1 && this.turnTicks < HATCH_TURN_OPEN_TICKS) {
                this.turnTicks++;
                if (this.turnTicks >= HATCH_TURN_OPEN_TICKS) {
                    this.valveTicks = -1;
                    this.turnTicks = -1;
                }
            }
        } else {
            if (this.turnTicks != -1 && this.turnTicks < HATCH_TURN_CLOSE_TICKS) {
                this.turnTicks++;
            } else if (this.valveTicks != -1 && this.valveTicks < HATCH_VALVE_CLOSE_TICKS) {
                this.valveTicks++;
                if (this.valveTicks >= HATCH_VALVE_CLOSE_TICKS) {
                    this.turnTicks = -1;
                    this.valveTicks = -1;
                }
            }
        }
    }

    public boolean canSwitchState() {
        return this.animationTicks <= 0;
    }

    public boolean mayPass() {
        return this.open && this.turnTicks == -1;
    }

    public float getOpen(float pt) {
        if (this.turnTicks < 0) return this.open ? 1.0F : 0.0F;
        if (this.open && this.turnTicks == 0 && this.valveTicks < HATCH_VALVE_OPEN_TICKS) return 0.0F;
        float transition = this.turnTicks + pt;
        return Math.clamp(this.open ? transition / HATCH_TURN_OPEN_TICKS : 1.0F-(transition / HATCH_TURN_CLOSE_TICKS), 0.0F, 1.0F);
    }
    public float getValve(float pt) {
        if (this.valveTicks < 0) return 0.0F;
        if (!this.open && this.valveTicks == 0 && this.turnTicks < HATCH_TURN_CLOSE_TICKS) return 0.0F;
        float transition = this.valveTicks + pt;
        return Math.clamp(this.open ? transition / HATCH_VALVE_OPEN_TICKS : 1.0F-(transition / HATCH_VALVE_CLOSE_TICKS), 0.0F, 1.0F);
    }

    public void setOpen(boolean open) {
        if (this.level instanceof ServerLevel level) {
            BlockPos pos = this.worldPosition;
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos),
                    new PacketSealedHatch(pos.getX(), pos.getY(), pos.getZ(), open));
        }

        this.valveTicks = 0;
        this.turnTicks = 0;
        this.open = open;
        this.animationTicks = open ? 90 : 95;
    }

    private AABB cachedBB = null;

    public AABB getCachedBB() {
        if (this.cachedBB == null) this.cachedBB = this.createBB();
        return this.cachedBB;
    }

    private AABB createBB() {
        BlockPos pos = this.worldPosition;
        return new AABB(
                pos.getX() - 1, pos.getY() - 0.125, pos.getZ() - 1,
                pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2
        );
    }

    public void invalidateBB() {
        this.cachedBB = null;
    }
}
