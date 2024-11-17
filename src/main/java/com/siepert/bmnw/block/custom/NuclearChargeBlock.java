package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.entity.custom.NuclearChargeEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NuclearChargeBlock extends Block implements IDetonatable {
    public NuclearChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        NuclearChargeEntity entity = new NuclearChargeEntity(ModEntityTypes.NUCLEAR_CHARGE.get(), level);
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
        level.addFreshEntity(entity);
    }
}
