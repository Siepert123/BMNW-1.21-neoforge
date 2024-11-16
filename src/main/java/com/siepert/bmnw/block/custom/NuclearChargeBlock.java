package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.block.entity.ModBlockEntities;
import com.siepert.bmnw.block.entity.custom.NuclearChargeBlockEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NuclearChargeBlock extends Block implements EntityBlock, IDetonatable {
    public NuclearChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        ((NuclearChargeBlockEntity)level.getBlockEntity(pos)).detonate();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NuclearChargeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == ModBlockEntities.NUCLEAR_CHARGE.get() ? NuclearChargeBlockEntity::tick : null;
    }
}
