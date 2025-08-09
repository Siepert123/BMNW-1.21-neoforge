package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public record PacketSendNuclearSound(Vec3 source, NukeType nukeType) implements CustomPacketPayload {
    public static final Type<PacketSendNuclearSound> TYPE = new Type<>(BMNW.namespace("send_nuclear_sound"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, PacketSendNuclearSound> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PacketSendNuclearSound decode(ByteBuf buffer) {
            Vec3 source = new Vec3(
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble()
            );
            NukeType type = BMNWResourceKeys.NUKE_TYPE_REGISTRY.byIdOrThrow(Byte.toUnsignedInt(buffer.readByte()));
            return new PacketSendNuclearSound(source, type);
        }

        @Override
        public void encode(ByteBuf buffer, PacketSendNuclearSound value) {
            buffer.writeDouble(value.source.x);
            buffer.writeDouble(value.source.y);
            buffer.writeDouble(value.source.z);
            buffer.writeByte(BMNWResourceKeys.NUKE_TYPE_REGISTRY.getId(value.nukeType));
        }
    };

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            int dist = this.nukeType.getSoundDistance();
            double playerDist = Math.sqrt(player.distanceToSqr(this.source));
            if (playerDist <= dist) {
                double inv = dist - playerDist;
                double volume = Math.min(1.0F, inv*2 / dist);
                double pitch = inv / dist;
                player.playSound(this.nukeType.getExplosionSound(), (float) volume, (float) pitch);
            }
        });
    }
}
