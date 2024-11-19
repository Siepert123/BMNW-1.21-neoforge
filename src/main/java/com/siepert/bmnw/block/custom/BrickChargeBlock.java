package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.entity.custom.BlockDebrisEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BrickChargeBlock extends Block implements IDetonatable {
    public BrickChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4.0f, Level.ExplosionInteraction.TNT);
        int bricks = level.random.nextInt(50) + 50;
        for (int i = 0; i < bricks; i++) {
            BlockDebrisEntity entity = new BlockDebrisEntity(ModEntityTypes.BLOCK_DEBRIS.get(), level);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entity.setDeltaMovement(new Vec3((level.random.nextDouble() - level.random.nextDouble())*5,
                    (level.random.nextDouble() - level.random.nextDouble())*5,
                    (level.random.nextDouble() - level.random.nextDouble())*5));
            int b = level.random.nextInt(5);
            if (b == 0) {
                entity.setDebrisState(Blocks.NETHER_BRICKS.defaultBlockState());
            } else if (b == 1 || b == 2) {
                entity.setDebrisState(Blocks.STONE_BRICKS.defaultBlockState());
            } else if (b == 3) {
                entity.setDebrisState(Blocks.END_STONE_BRICKS.defaultBlockState());
            } else {
                entity.setDebrisState(Blocks.BRICKS.defaultBlockState());
            }
            level.addFreshEntity(entity);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) {
            detonate(level, pos);
        }
    }
}
