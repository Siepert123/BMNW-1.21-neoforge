package nl.melonstudios.bmnw.block.container.fluid;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FluidFlow implements StringRepresentable {
    IN_ONLY(true, false),
    OUT_ONLY(false, true),
    BUFFER(true, true),
    QUARANTINE(false, false);

    public final boolean in, out;
    private final String name;
    FluidFlow(boolean in, boolean out) {
        this.in = in;
        this.out = out;
        this.name = this.toString().toLowerCase(Locale.ENGLISH);
    }

    public static final FluidFlow[] VALUES = values();
    public static final Codec<FluidFlow> CODEC = StringRepresentable.fromEnum(() -> VALUES);
    public static final StreamCodec<ByteBuf, FluidFlow> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FluidFlow decode(ByteBuf buffer) {
            return VALUES[buffer.readByte()];
        }

        @Override
        public void encode(ByteBuf buffer, FluidFlow value) {
            buffer.writeByte(value.ordinal());
        }
    };

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
