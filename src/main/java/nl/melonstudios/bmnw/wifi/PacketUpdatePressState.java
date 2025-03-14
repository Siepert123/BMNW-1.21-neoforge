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
import nl.melonstudios.bmnw.block.entity.PressBlockEntity;

public record PacketUpdatePressState(int x, int y, int z) implements CustomPacketPayload {
    public static final Type<PacketUpdatePressState> TYPE = new Type<>(BMNW.namespace("packet_update_press_state"));
    public static final StreamCodec<ByteBuf, PacketUpdatePressState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PacketUpdatePressState::x,
            ByteBufCodecs.INT,
            PacketUpdatePressState::y,
            ByteBufCodecs.INT,
            PacketUpdatePressState::z,
            PacketUpdatePressState::new
    );

    public static void handle(PacketUpdatePressState packet, IPayloadContext context) {
        Level level = context.player().level();
        BlockPos pos = new BlockPos(packet.x, packet.y, packet.z);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PressBlockEntity press) press.progress = 38;

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
