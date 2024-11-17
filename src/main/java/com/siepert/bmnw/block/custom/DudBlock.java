package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.entity.custom.DudEntity;
import com.siepert.bmnw.entity.custom.NuclearChargeEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DudBlock extends Block implements IDetonatable {
    public DudBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        DudEntity entity = new DudEntity(ModEntityTypes.DUD.get(), level);
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
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
}
