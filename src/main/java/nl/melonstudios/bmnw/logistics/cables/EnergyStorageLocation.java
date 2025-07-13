package nl.melonstudios.bmnw.logistics.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public record EnergyStorageLocation(BlockPos pos, Direction face) {
    public IEnergyStorage getEnergyStorageAt(Level level) {
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, this.pos, this.face);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("pos", this.pos.asLong());
        nbt.putByte("face", (byte)this.face.get3DDataValue());
        return nbt;
    }
    @Nullable
    public static EnergyStorageLocation read(CompoundTag nbt) {
        if (!nbt.contains("pos", Tag.TAG_LONG) || !nbt.contains("face", Tag.TAG_BYTE)) return null;
        return new EnergyStorageLocation(
                BlockPos.of(nbt.getLong("pos")),
                Direction.from3DDataValue(nbt.getByte("face"))
        );
    }
}
