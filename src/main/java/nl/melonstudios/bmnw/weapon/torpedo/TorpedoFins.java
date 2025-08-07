package nl.melonstudios.bmnw.weapon.torpedo;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public record TorpedoFins(float inaccuracy) {
    public void write(ByteBuf buf) {
        buf.writeFloat(this.inaccuracy);
    }
    public static TorpedoFins read(ByteBuf buf) {
        return new TorpedoFins(buf.readFloat());
    }

    public void write(CompoundTag nbt) {
        nbt.putFloat("inaccuracy", this.inaccuracy);
    }
    public static TorpedoFins read(CompoundTag nbt) {
        return new TorpedoFins(nbt.getFloat("inaccuracy"));
    }
}
