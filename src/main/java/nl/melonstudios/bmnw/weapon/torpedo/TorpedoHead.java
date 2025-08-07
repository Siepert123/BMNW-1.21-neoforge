package nl.melonstudios.bmnw.weapon.torpedo;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public record TorpedoHead(float explosionPower, boolean fire) {
    public void write(ByteBuf buf) {
        buf.writeFloat(this.explosionPower);
        buf.writeBoolean(this.fire);
    }
    public static TorpedoHead read(ByteBuf buf) {
        return new TorpedoHead(buf.readFloat(), buf.readBoolean());
    }

    public void write(CompoundTag nbt) {
        nbt.putFloat("explosionPower", this.explosionPower);
        nbt.putBoolean("fire", this.fire);
    }
    public static TorpedoHead read(CompoundTag nbt) {
        return new TorpedoHead(nbt.getFloat("explosionPower"), nbt.getBoolean("fire"));
    }
}
