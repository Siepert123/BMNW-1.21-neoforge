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
import nl.melonstudios.bmnw.block.entity.SealedHatchBlockEntity;

public record PacketSealedHatch(int x, int y, int z, boolean open) implements CustomPacketPayload {
    public static final Type<PacketSealedHatch> TYPE = new Type<>(BMNW.namespace("packet_sealed_hatch"));
    public static final StreamCodec<ByteBuf, PacketSealedHatch> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PacketSealedHatch::x,
            ByteBufCodecs.INT,
            PacketSealedHatch::y,
            ByteBufCodecs.INT,
            PacketSealedHatch::z,
            ByteBufCodecs.BOOL,
            PacketSealedHatch::open,
            PacketSealedHatch::new
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
            if (be instanceof SealedHatchBlockEntity hatch) {
                hatch.setOpen(this.open);
            }
        });
    }
}
