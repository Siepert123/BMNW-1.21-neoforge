package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.effect.ModEffects;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DecontaminatorBlock extends Block {
    public DecontaminatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living) {
            RadHelper.removeEntityRadiation(living, UnitConvertor.fromMilli(100));

            if (living.hasEffect(ModEffects.CONTAMINATION)) {
                living.removeEffect(ModEffects.CONTAMINATION);
            }
        }
    }
}