package com.melonstudios.bmnw.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NuclearWasteBarrelBlock extends SimpleRadioactiveBlock {
    public NuclearWasteBarrelBlock(Properties properties, float rads) {
        super(properties, rads);
    }

    private static final VoxelShape shape = Block.box(2, 0, 2, 14, 16, 14);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        level.addParticle(ParticleTypes.MYCELIUM,
                0.25 + pos.getX() + random.nextDouble() / 2, pos.getY()+1, 0.25 + pos.getZ() + random.nextDouble() / 2,
                0, 0, 0);
        level.addParticle(ParticleTypes.MYCELIUM,
                0.25 + pos.getX() + random.nextDouble() / 2, pos.getY()+1, 0.25 + pos.getZ() + random.nextDouble() / 2,
                0, 0, 0);
    }
}
