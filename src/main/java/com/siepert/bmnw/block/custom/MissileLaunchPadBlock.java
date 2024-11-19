package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MissileLaunchPadBlock extends Block implements EntityBlock, IDetonatable {
    public MissileLaunchPadBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MissileLaunchPadBlockEntity(pos, state);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        MissileLaunchPadBlockEntity be = (MissileLaunchPadBlockEntity) level.getBlockEntity(pos);
        if (be != null && be.canLaunch()) {
            be.launch();
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide() && level.hasNeighborSignal(pos)) {
            detonate(level, pos);
        }
    }
}
