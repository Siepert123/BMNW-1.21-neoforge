package nl.melonstudios.bmnw.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

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
}
