package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.BMNWEntityTypes;
import com.siepert.bmnw.entity.custom.NuclearChargeEntity;
import com.siepert.bmnw.interfaces.IBombBlock;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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
