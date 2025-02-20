package nl.melonstudios.bmnw.block.custom;

import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.entity.custom.NuclearChargeEntity;
import nl.melonstudios.bmnw.interfaces.IBombBlock;
import nl.melonstudios.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class NuclearChargeBlock extends Block implements IDetonatable, IBombBlock {
    public NuclearChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        NuclearChargeEntity entity = new NuclearChargeEntity(BMNWEntityTypes.NUCLEAR_CHARGE.get(), level);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        level.addFreshEntity(entity);
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
