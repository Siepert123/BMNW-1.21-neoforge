package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.PartialModel;
import nl.melonstudios.bmnw.weapon.nuke.BMNWNukeTypes;
import nl.melonstudios.bmnw.weapon.nuke.NukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class CaseohNukeBlock extends NukeBlock {
    public CaseohNukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PartialModel getDroppedModel() {
        return BMNWPartialModels.NUKE_CASEOH;
    }

    @Override
    public NukeType getNukeType() {
        return BMNWNukeTypes.CASEOH.get();
    }

    @Override
    public boolean isReady(Level level, BlockPos pos) {
        return true;
    }

    @Override
    public void playDetonationSound(Level level, Vec3 pos) {

    }
}
