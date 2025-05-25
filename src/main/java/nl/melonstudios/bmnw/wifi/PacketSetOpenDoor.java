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
import nl.melonstudios.bmnw.interfaces.IOpenDoor;

public record PacketSetOpenDoor(int x, int y, int z, boolean open) implements CustomPacketPayload {
    public static final Type<PacketSetOpenDoor> TYPE = new Type<>(BMNW.namespace("packet_set_open_door"));
    public static final StreamCodec<ByteBuf, PacketSetOpenDoor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PacketSetOpenDoor::x,
            ByteBufCodecs.INT,
            PacketSetOpenDoor::y,
            ByteBufCodecs.INT,
            PacketSetOpenDoor::z,
            ByteBufCodecs.BOOL,
            PacketSetOpenDoor::open,
            PacketSetOpenDoor::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockPos pos = new BlockPos(this.x, this.y, this.z);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IOpenDoor door) door.setOpen(this.open);
        });
    }

    public static PacketSetOpenDoor of(BlockPos pos, boolean open) {
        return new PacketSetOpenDoor(pos.getX(), pos.getY(), pos.getZ(), open);
    }
}
