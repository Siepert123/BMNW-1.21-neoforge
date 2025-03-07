package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;

import javax.annotation.Nonnull;

public record PacketMushroomCloud(boolean soul, double x, double y, double z, float size) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketMushroomCloud> TYPE = new Type<>(BMNW.namespace("packet_mushroom_cloud"));

    public static final StreamCodec<ByteBuf, PacketMushroomCloud> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PacketMushroomCloud::soul,
            ByteBufCodecs.DOUBLE,
            PacketMushroomCloud::x,
            ByteBufCodecs.DOUBLE,
            PacketMushroomCloud::y,
            ByteBufCodecs.DOUBLE,
            PacketMushroomCloud::z,
            ByteBufCodecs.FLOAT,
            PacketMushroomCloud::size,
            PacketMushroomCloud::new
    );

    public static void handle(PacketMushroomCloud packet, IPayloadContext context) {
        Level level = context.player().level();
        level.addParticle(BMNWParticleTypes.EVIL_FOG.get(), packet.x, packet.y, packet.z, 0, packet.size, 0);
    }


    @Override
    @Nonnull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
