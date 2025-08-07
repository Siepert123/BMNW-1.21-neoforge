package nl.melonstudios.bmnw.weapon.torpedo;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public record TorpedoStats(TorpedoThruster thruster, TorpedoFins fins, TorpedoHead head) {
    public static final TorpedoStats EMPTY = new TorpedoStats(null, null, null);

    public boolean isValid() {
        return this != EMPTY;
    }

    public void write(ByteBuf buf) {
        if (!this.isValid()) throw new IllegalStateException("Cannot write invalid torpedo statistics");
        this.thruster.write(buf);
        this.fins.write(buf);
        this.head.write(buf);
    }
    public static TorpedoStats read(ByteBuf buf) {
        return new TorpedoStats(
                TorpedoThruster.read(buf),
                TorpedoFins.read(buf),
                TorpedoHead.read(buf)
        );
    }

    public CompoundTag write(CompoundTag nbt) {
        if (!this.isValid()) return nbt;
        nbt.putBoolean("validator", true);
        CompoundTag thruster = new CompoundTag();
        this.thruster.write(thruster);
        nbt.put("Thruster", thruster);
        CompoundTag fins = new CompoundTag();
        this.fins.write(fins);
        nbt.put("Fins", fins);
        CompoundTag head = new CompoundTag();
        this.head.write(head);
        nbt.put("Head", head);
        return nbt;
    }
    public static TorpedoStats read(CompoundTag nbt) {
        if (!nbt.getBoolean("validator")) return EMPTY;
        return new TorpedoStats(
                TorpedoThruster.read(nbt.getCompound("Thruster")),
                TorpedoFins.read(nbt.getCompound("Fins")),
                TorpedoHead.read(nbt.getCompound("Head"))
        );
    }
}
