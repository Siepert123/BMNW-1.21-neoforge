package nl.melonstudios.bmnw.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public record PipeFluidHandlerGetter(BlockPos pos, Direction face, int tanks) {
    public IFluidHandler getFluidHandler(Level level) {
        return level.getCapability(Capabilities.FluidHandler.BLOCK, this.pos, this.face);
    }

    public boolean stillValid(Level level) {
        return this.getFluidHandler(level) != null;
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());

        nbt.putByte("face", (byte)this.face.get3DDataValue());

        nbt.putInt("tanks", this.tanks);

        return nbt;
    }

    public static PipeFluidHandlerGetter deserialize(CompoundTag nbt) {
        BlockPos pos = new BlockPos(
                nbt.getInt("x"),
                nbt.getInt("y"),
                nbt.getInt("z")
        );
        Direction face = Direction.from3DDataValue(
                nbt.getByte("face")
        );
        int tanks = nbt.getInt("tanks");

        return new PipeFluidHandlerGetter(pos, face, tanks);
    }
}
