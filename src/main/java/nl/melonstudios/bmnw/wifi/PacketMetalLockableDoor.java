package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.entity.MetalLockableDoorBlockEntity;

public record PacketMetalLockableDoor(int x, int y, int z, boolean open) implements CustomPacketPayload {
    public static final Type<PacketMetalLockableDoor> TYPE = new Type<>(BMNW.namespace("packet_metal_lock_door"));
    public static final StreamCodec<ByteBuf, PacketMetalLockableDoor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PacketMetalLockableDoor::x,
            ByteBufCodecs.INT,
            PacketMetalLockableDoor::y,
            ByteBufCodecs.INT,
            PacketMetalLockableDoor::z,
            ByteBufCodecs.BOOL,
            PacketMetalLockableDoor::open,
            PacketMetalLockableDoor::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockPos pos = new BlockPos(this.x, this.y, this.z);
            if (level.getBlockEntity(pos) instanceof MetalLockableDoorBlockEntity door) {
                door.setOpen(this.open);
            }
        });
    }
}
