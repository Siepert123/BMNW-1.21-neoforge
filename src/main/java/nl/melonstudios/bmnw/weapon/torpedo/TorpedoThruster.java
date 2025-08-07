package nl.melonstudios.bmnw.weapon.torpedo;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public record TorpedoThruster(float efficiency) {
    public void write(ByteBuf buf) {
        buf.writeFloat(this.efficiency);
    }
    public static TorpedoThruster read(ByteBuf buf) {
        return new TorpedoThruster(buf.readFloat());
    }

    public void write(CompoundTag nbt) {
        nbt.putFloat("efficiency", this.efficiency);
    }
    public static TorpedoThruster read(CompoundTag nbt) {
        return new TorpedoThruster(nbt.getFloat("efficiency"));
    }
}
