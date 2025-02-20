package nl.melonstudios.bmnw.block.custom;

import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NuclearRemainsBlock extends SimpleRadioactiveBlock {
    private static final int decayChanceTo0 = 2;
    private final BlockState decay;
    public NuclearRemainsBlock(Properties properties, float rads, BlockState decay) {
        super(properties, rads);
        this.decay = decay;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setRemainingFireTicks(20);

        if (entity instanceof LivingEntity living) {
            living.addEffect(
                    new MobEffectInstance(
                            BMNWEffects.CONTAMINATION,
                            600,
                            1
                    )
            );
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.is(BMNWBlocks.NUCLEAR_REMAINS.get())) {
            boolean decayable = true;
            for (Direction direction : Direction.values()) {
                if (level.getBlockState(pos.offset(direction.getNormal())).is(BMNWBlocks.BLAZING_NUCLEAR_REMAINS.get())) {
                    decayable = false;
                }
            }
            if (decayable) {
                level.setBlock(pos, decay, 3);
            }
        } else {
            level.setBlock(pos, decay, 3);
        }
    }

    private static final boolean animate = false;
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!animate) return;
        if (state.is(BMNWBlocks.BLAZING_NUCLEAR_REMAINS.get())) {
            if (random.nextFloat() > 0.8) {
                level.addParticle(ParticleTypes.LAVA,
                        pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(),
                        0, 0, 0);
            }
        }
        level.addParticle(ParticleTypes.FLAME,
                pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(),
                0, 0, 0);
    }
}
