package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.client.BMNWClientBouncer;
import nl.melonstudios.bmnw.misc.DistrictHolder;

public record PacketSendShockwave(long initTime, float x, float z, int max) implements CustomPacketPayload {
    public static final Type<PacketSendShockwave> TYPE = new Type<>(BMNW.namespace("send_shockwave"));
    public static final StreamCodec<ByteBuf, PacketSendShockwave> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            PacketSendShockwave::initTime,
            ByteBufCodecs.FLOAT,
            PacketSendShockwave::x,
            ByteBufCodecs.FLOAT,
            PacketSendShockwave::z,
            ByteBufCodecs.VAR_INT,
            PacketSendShockwave::max,
            PacketSendShockwave::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        if (DistrictHolder.isClient()) {
            BMNWClientBouncer.addShockwave(context.player().level(), this.initTime, this.x, this.z, this.max);
        }
    }
}
