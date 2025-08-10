package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.particle.type.ResizableParticleOptions;

import javax.annotation.Nonnull;

public record PacketMushroomCloud(boolean soul, double x, double y, double z, float size) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketMushroomCloud> TYPE = new Type<>(BMNW.namespace("packet_mushroom_cloud"));
    @Override
    @Nonnull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
        ParticleType<ResizableParticleOptions> particle = packet.soul ?
                BMNWParticleTypes.SOUL_MUSHROOM_CLOUD.get() :
                BMNWParticleTypes.MUSHROOM_CLOUD.get();
        ResizableParticleOptions optionsParticle = new ResizableParticleOptions(particle, packet.size * 0.1F);
        ParticleType<ResizableParticleOptions> smoke = BMNWParticleTypes.MUSHROOM_SMOKE.get();
        ResizableParticleOptions optionsSmoke = new ResizableParticleOptions(smoke, packet.size * 0.1F);
        ParticleType<ResizableParticleOptions> flash = BMNWParticleTypes.FLASH.get();
        ResizableParticleOptions optionsFlash = new ResizableParticleOptions(flash, packet.size*10);
        double degreePerPart = 1;
        final double x = packet.x;
        final double y = packet.y;
        final double z = packet.z;
        double vx, vy, vz;
        double yaw = 0;
        double pitch = 0;
        double stem = packet.size * 0.5;
        for (float i = 0; i < 30; i++) {
            double delta = 1.0 - (i / 30.0);
            level.addParticle(optionsParticle, x, y, z, 0, i*packet.size * 0.15, 0);
            level.addParticle(optionsParticle, x, y, z, stem*delta, i*packet.size * 0.15, 0);
            level.addParticle(optionsParticle, x, y, z, -stem*delta, i*packet.size * 0.15, 0);
            level.addParticle(optionsParticle, x, y, z, 0, i*packet.size * 0.15, stem*delta);
            level.addParticle(optionsParticle, x, y, z, 0, i*packet.size * 0.15, -stem*delta);
        }

        double aThirdSize = (packet.size / 3) * 5;
        double aTwelfthSize = (packet.size / 12) * 5;
        while (yaw < 360) {
            while (pitch < 180) {
                vx = relativeX(yaw, pitch, aThirdSize);
                vz = relativeZ(yaw, pitch, aThirdSize);
                vy = relativeY(yaw, pitch, aTwelfthSize);
                level.addParticle(optionsParticle, x, y, z, vx, vy+packet.size*5, vz);
                pitch += degreePerPart * 10;
            }
            pitch = 0;
            yaw += degreePerPart * 10;
        }

        double ringPos = packet.size*3.5;
        yaw = 0;
        while (yaw < 360) {
            vx = relativeX(yaw, 90, packet.size*1.5);
            vz = relativeZ(yaw, 90, packet.size*1.5);
            level.addParticle(optionsSmoke, x, y, z, vx, ringPos, vz);
            yaw += degreePerPart * 5;
        }

        ringPos = packet.size * 5.5;
        yaw = 0;
        while (yaw < 360) {
            vx = relativeX(yaw, 90, packet.size*3);
            vz = relativeZ(yaw, 90, packet.size*3);
            level.addParticle(optionsSmoke, x, y, z, vx, ringPos, vz);
            yaw += degreePerPart;
        }

        level.addParticle(optionsFlash, x, y, z, 0, 0, 0);
    }

    private static double relativeX(double y, double p, double r) {
        return Math.sin(Math.toRadians(y)) * r * Math.sin(Math.toRadians(p));
    }
    private static double relativeY(double y, double p, double r) {
        return Math.cos(Math.toRadians(p)) * r;
    }
    private static double relativeZ(double y, double p, double r) {
        return Math.cos(Math.toRadians(y)) * r * Math.sin(Math.toRadians(p));
    }
}
