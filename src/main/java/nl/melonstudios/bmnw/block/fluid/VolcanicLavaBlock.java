package nl.melonstudios.bmnw.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.common.Tags;

public class VolcanicLavaBlock extends LiquidBlock {
    public VolcanicLavaBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.setRemainingFireTicks(1200);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock()) || movedByPiston) return;
        if (!newState.getFluidState().isEmpty()) level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 3);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        for (Direction d : Direction.values()) {
            if (d == Direction.DOWN) continue;
            if (level.getBlockState(pos.relative(d)).getFluidState().is(Tags.Fluids.WATER)) {
                level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 3);
                return;
            }
        }
    }
}
