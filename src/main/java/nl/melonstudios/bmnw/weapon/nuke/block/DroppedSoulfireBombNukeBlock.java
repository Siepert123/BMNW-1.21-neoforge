package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.weapon.nuke.BMNWNukeTypes;
import nl.melonstudios.bmnw.weapon.nuke.NukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;
import org.jetbrains.annotations.Nullable;

public class DroppedSoulfireBombNukeBlock extends NukeBlock implements EntityBlock {
    public DroppedSoulfireBombNukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public NukeType getNukeType() {
        return BMNWNukeTypes.DROPPED_SOULFIRE_BOMB.get();
    }

    @Override
    public boolean isReady(Level level, BlockPos pos) {
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean fancyDrop() {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DroppedSoulfireBombBE(pos, state);
    }
}
