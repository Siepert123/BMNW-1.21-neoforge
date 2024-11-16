package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.effect.ModEffects;
import net.minecraft.core.BlockPos;
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
    public NuclearRemainsBlock(Properties properties, long femtoRads, BlockState decay) {
        super(properties, femtoRads);
        this.decay = decay;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setRemainingFireTicks(20);

        if (entity instanceof LivingEntity living) {
            living.addEffect(
                    new MobEffectInstance(
                            ModEffects.CONTAMINATION,
                            600,
                            1
                    )
            );
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(decayChanceTo0) == 0) {
            level.setBlock(pos, decay, 3);
        }
    }
}
