package nl.melonstudios.bmnw.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import nl.melonstudios.bmnw.init.BMNWEffects;

public class CancerEffect extends MobEffect {
    public CancerEffect() {
        super(MobEffectCategory.HARMFUL, 0xAD114D);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.addEffect(new MobEffectInstance(
                BMNWEffects.CANCER, 20*60, amplifier + (livingEntity.getRandom().nextDouble() < 0.000001 ? 1 : 0)
        ));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
