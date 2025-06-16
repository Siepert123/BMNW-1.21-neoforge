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
import nl.melonstudios.bmnw.block.entity.ExtendableCatwalkBlockEntity;

public record PacketExtendableCatwalk(BlockPos pos, boolean extended) implements CustomPacketPayload {
    public static final Type<PacketExtendableCatwalk> TYPE = new Type<>(BMNW.namespace("extendable_catwalk"));
    public static final StreamCodec<ByteBuf, PacketExtendableCatwalk> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketExtendableCatwalk::pos,
            ByteBufCodecs.BOOL,
            PacketExtendableCatwalk::extended,
            PacketExtendableCatwalk::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        Level level = context.player().level();
        context.enqueueWork(() -> {
            BlockEntity be = level.getBlockEntity(this.pos);
            if (be instanceof ExtendableCatwalkBlockEntity catwalk) {
                catwalk.acceptPacket(this);
            }
        });
    }
}
