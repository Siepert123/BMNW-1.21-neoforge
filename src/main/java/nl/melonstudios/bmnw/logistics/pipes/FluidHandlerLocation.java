package nl.melonstudios.bmnw.logistics.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public record FluidHandlerLocation(BlockPos pos, Direction face) {
    public IFluidHandler getFluidHandlerAt(Level level) {
        return level.getCapability(Capabilities.FluidHandler.BLOCK, this.pos, this.face);
    }

    public boolean stillValid(Level level) {
        return level.getCapability(Capabilities.FluidHandler.BLOCK, this.pos, this.face) != null;
    }

    @Contract(pure = true)
    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("pos", this.pos.asLong());
        nbt.putByte("face", (byte)this.face.get3DDataValue());
        return nbt;
    }
    @Nullable
    public static FluidHandlerLocation read(CompoundTag nbt) {
        if (!nbt.contains("pos", Tag.TAG_LONG) || !nbt.contains("face", Tag.TAG_BYTE)) return null;
        return new FluidHandlerLocation(
                BlockPos.of(nbt.getLong("pos")),
                Direction.from3DDataValue(nbt.getByte("face"))
        );
    }
}