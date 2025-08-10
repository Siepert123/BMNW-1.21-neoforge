package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.weapon.nuke.BMNWNukeTypes;
import nl.melonstudios.bmnw.weapon.nuke.NukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class TsarBombaNukeBlock extends NukeBlock {
    public TsarBombaNukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public NukeType getNukeType() {
        return BMNWNukeTypes.TSAR_BOMBA.get();
    }

    @Override
    public boolean isReady(Level level, BlockPos pos) {
        return true;
    }
}
