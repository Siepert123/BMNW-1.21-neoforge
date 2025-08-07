package nl.melonstudios.bmnw.weapon.missile.guidance.prec;

import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;
import nl.melonstudios.bmnw.weapon.missile.guidance.GuidanceComputer;
import nl.melonstudios.bmnw.weapon.missile.guidance.GuidanceProgram;

public class StraightUpGuidanceProgram implements GuidanceProgram {
    @Override
    public void doTheGuidanceProgramThings(CustomizableMissileEntity missile, GuidanceComputer computer) {
        missile.setXRot(90);
        missile.boostMode = true;
    }

    @Override
    public int getHardcodedID() {
        return 1;
    }
}
