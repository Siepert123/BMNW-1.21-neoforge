package nl.melonstudios.bmnw.wifi;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWDataComponents;

public record PacketFluidIdentifier(String fluidID) implements CustomPacketPayload {
    public static final Type<PacketFluidIdentifier> TYPE = new Type<>(BMNW.namespace("fluid_identifier"));
    public static final StreamCodec<ByteBuf, PacketFluidIdentifier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PacketFluidIdentifier::fluidID,
            PacketFluidIdentifier::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().getItemInHand(InteractionHand.MAIN_HAND).set(BMNWDataComponents.FLUID_TYPE, this.fluidID);
        });
    }
}
