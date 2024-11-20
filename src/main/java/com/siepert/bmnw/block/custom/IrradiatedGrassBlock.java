package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.effect.ModEffects;
import com.siepert.bmnw.misc.ModStateProperties;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class IrradiatedGrassBlock extends Block {
    public static final IntegerProperty RAD_LEVEL = ModStateProperties.RAD_LEVEL;
    public IrradiatedGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RAD_LEVEL);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(
                    ModEffects.CONTAMINATION,
                    400,
                    state.getValue(RAD_LEVEL)-1
            ));
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i <= state.getValue(RAD_LEVEL); i++) {
            level.addParticle(ParticleTypes.MYCELIUM,
                    pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(),
                    0, 0, 0);
        }
    }
}
