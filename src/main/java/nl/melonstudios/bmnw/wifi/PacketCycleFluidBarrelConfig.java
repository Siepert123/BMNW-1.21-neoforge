package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.interfaces.IFlowCfg;

public record PacketCycleFluidBarrelConfig(BlockPos pos, boolean redstone) implements CustomPacketPayload {
    public static final Type<PacketCycleFluidBarrelConfig> TYPE = new Type<>(BMNW.namespace("cycle_fluid_barrel_cfg"));
    public static final StreamCodec<ByteBuf, PacketCycleFluidBarrelConfig> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketCycleFluidBarrelConfig::pos,
            ByteBufCodecs.BOOL,
            PacketCycleFluidBarrelConfig::redstone,
            PacketCycleFluidBarrelConfig::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.getBlockEntity(this.pos) instanceof IFlowCfg be) {
                be.cycle(this.redstone);
            }
        });
    }
}
