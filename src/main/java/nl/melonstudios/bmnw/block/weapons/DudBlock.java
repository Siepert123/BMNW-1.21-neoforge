package nl.melonstudios.bmnw.block.weapons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.entity.DudEntity;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.interfaces.IBombBlock;
import nl.melonstudios.bmnw.interfaces.IDetonatable;

public class DudBlock extends Block implements IDetonatable, IBombBlock {
    public DudBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        DudEntity entity = new DudEntity(BMNWEntityTypes.DUD.get(), level);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        level.addFreshEntity(entity);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        detonate(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            if (level.hasNeighborSignal(pos)) {
                detonate(level, pos);
            }
        }
    }

    @Override
    public int radius() {
        return 32;
    }
}
