package nl.melonstudios.bmnw.weapon.missile.guidance;

import net.minecraft.nbt.CompoundTag;
import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;

import javax.annotation.Nullable;

public class CompiledGuidanceProgram implements GuidanceProgram {
    @Override
    public void doTheGuidanceProgramThings(CustomizableMissileEntity missile, GuidanceComputer computer) {

    }

    @Override
    public int getHardcodedID() {
        return -1;
    }

    public CompoundTag serialize(CompoundTag nbt) {
        return nbt;
    }
    @Nullable
    public static CompiledGuidanceProgram createFromNBT(CompoundTag nbt) {
        return null;
    }
}
