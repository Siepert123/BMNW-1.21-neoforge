package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import nl.melonstudios.bmnw.init.BMNWBlocks;

public class HotMeteoriteBlock extends Block {
    public HotMeteoriteBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.isAir()) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, BMNWBlocks.METEORITE_COBBLESTONE.get().defaultBlockState(), 3);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.above()).canOcclude()) {
            level.addParticle(
                    ParticleTypes.LAVA,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1,
                    pos.getZ() + random.nextDouble(),
                    0, 0, 0
            );
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 5;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setRemainingFireTicks(100);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        for (Direction direction : Direction.values()) {
            if (level.getFluidState(pos.relative(direction)).is(Tags.Fluids.WATER)) {
                level.setBlock(pos, BMNWBlocks.METEORITE_COBBLESTONE.get().defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
                return;
            }
        }
    }
}
