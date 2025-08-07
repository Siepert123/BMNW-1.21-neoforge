package nl.melonstudios.bmnw.weapon.missile.guidance.prec;

import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;
import nl.melonstudios.bmnw.weapon.missile.guidance.GuidanceComputer;
import nl.melonstudios.bmnw.weapon.missile.guidance.GuidanceProgram;

public class NullGuidanceProgram implements GuidanceProgram {
    @Override
    public void doTheGuidanceProgramThings(CustomizableMissileEntity missile, GuidanceComputer computer) {

    }

    @Override
    public int getHardcodedID() {
        return 0;
    }
}
