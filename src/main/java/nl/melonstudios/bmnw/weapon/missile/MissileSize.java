package nl.melonstudios.bmnw.weapon.missile;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum MissileSize implements StringRepresentable {
    SMALL("small", 0),
    MEDIUM("medium", 1),
    LARGE("large", 2);

    private final String name;
    private final byte id;
    MissileSize(@Nonnull String name, int id) {
        this.name = name;
        this.id = (byte) id;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }

    private static final MissileSize[] VALUES = {
            SMALL, MEDIUM, LARGE,
    };

    public static final Codec<MissileSize> CODEC = StringRepresentable.fromEnum(() -> VALUES);
    public static final StreamCodec<ByteBuf, MissileSize> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MissileSize decode(ByteBuf buffer) {
            return VALUES[buffer.readByte()];
        }

        @Override
        public void encode(ByteBuf buffer, MissileSize value) {
            buffer.writeByte(value.id);
        }
    };

    public MissileSize getOneSmaller() {
        return VALUES[this.id-1];
    }
}
