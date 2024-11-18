package com.siepert.bmnw.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NuclearWasteBarrelBlock extends SimpleRadioactiveBlock {
    public NuclearWasteBarrelBlock(Properties properties, long femtoRads) {
        super(properties, femtoRads);
    }

    private static final VoxelShape shape = Block.box(2, 0, 2, 14, 16, 14);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
}
