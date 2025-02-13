package com.melonstudios.bmnw.block.custom;

import com.melonstudios.bmnw.entity.BMNWEntityTypes;
import com.melonstudios.bmnw.entity.custom.CaseohEntity;
import com.melonstudios.bmnw.interfaces.IBombBlock;
import com.melonstudios.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CaseohBlock extends Block implements IDetonatable, IBombBlock {
    public CaseohBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        CaseohEntity entity = new CaseohEntity(BMNWEntityTypes.LITTLE_BOY.get(), level);
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

    private static final VoxelShape shape = Block.box(1, 1, 1, 15,15, 15);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public int radius() {
        return 128;
    }
}
