package nl.melonstudios.bmnw.weapon.missile.guidance;

import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;
import nl.melonstudios.bmnw.weapon.missile.guidance.prec.NullGuidanceProgram;
import nl.melonstudios.bmnw.weapon.missile.guidance.prec.StraightUpGuidanceProgram;

public interface GuidanceProgram {
    void doTheGuidanceProgramThings(CustomizableMissileEntity missile, GuidanceComputer computer) throws Throwable;

    /**
     * @return The hardcoded ID. -1 if it's a {@link CompiledGuidanceProgram}
     */
    int getHardcodedID();

    GuidanceProgram[] PRECOMPILED_GUIDANCE_PROGRAMS = {
            new NullGuidanceProgram(),
            new StraightUpGuidanceProgram()
    };
}
