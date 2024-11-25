package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.effect.BMNWEffects;
import com.siepert.bmnw.misc.BMNWStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class IrradiatedGrassBlock extends Block {
    public static final IntegerProperty RAD_LEVEL = BMNWStateProperties.RAD_LEVEL;
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
            for (ItemStack stack : livingEntity.getArmorSlots()) {
                if (stack.is(ItemTags.FOOT_ARMOR)) return;
            }
            livingEntity.addEffect(new MobEffectInstance(
                    BMNWEffects.CONTAMINATION,
                    400,
                    0
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
