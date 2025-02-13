package com.melonstudios.bmnw.block.custom;

import com.melonstudios.bmnw.block.BMNWBlocks;
import com.melonstudios.bmnw.misc.BMNWStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

public class IrradiatedLeavesBlock extends Block implements IShearable {
    public static final IntegerProperty RAD_LEVEL = BMNWStateProperties.RAD_LEVEL;
    public IrradiatedLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            for (int y = 1; y < 16; y++) {
                if (level.getBlockState(pos.below(y)).isFaceSturdy(level,pos.below(y), Direction.UP, SupportType.FULL)) {
                    if (level.getBlockState(pos.below(y-1)).canBeReplaced()) {
                        level.setBlock(pos.below(y - 1), BMNWBlocks.IRRADIATED_LEAF_PILE.get()
                                .defaultBlockState().setValue(RAD_LEVEL, state.getValue(RAD_LEVEL)), 3);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RAD_LEVEL);
    }

    private static final VoxelShape collision = Block.box(0, 0, 0, 16, 8, 16);

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return collision;
    }
}
