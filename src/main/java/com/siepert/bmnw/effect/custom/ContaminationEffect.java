package com.siepert.bmnw.effect.custom;

import com.siepert.bmnw.radiation.RadiationManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ContaminationEffect extends MobEffect {
    public ContaminationEffect() {
        super(MobEffectCategory.HARMFUL, 0x00dd00);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        RadiationManager.getInstance().addEntityRadiation(entity, amplifier+1);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
