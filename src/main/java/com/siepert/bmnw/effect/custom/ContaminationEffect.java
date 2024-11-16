package com.siepert.bmnw.effect.custom;

import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ContaminationEffect extends MobEffect {
    public ContaminationEffect() {
        super(MobEffectCategory.HARMFUL, 0x00dd00);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        RadHelper.addEntityRadiation(entity, UnitConvertor.fromMilli((amplifier+1)*100L));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}