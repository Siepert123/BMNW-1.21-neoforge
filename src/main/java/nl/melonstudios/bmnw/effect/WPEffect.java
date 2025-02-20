package nl.melonstudios.bmnw.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import nl.melonstudios.bmnw.init.BMNWEffects;
import nl.melonstudios.bmnw.init.BMNWDamageSources;

import java.util.Set;

public class WPEffect extends MobEffect {
    public WPEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFF2B2);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(BMNWDamageSources.wp_burns(livingEntity.level()), 2 + amplifier);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {

    }

    public static void inflictWP(LivingEntity entity, int level) {
        if (level == 0) {
            entity.addEffect(new MobEffectInstance(BMNWEffects.WHITE_PHOSPHORUS, 200, 0, true, true));
        } else if (level == 1) {
            entity.addEffect(new MobEffectInstance(BMNWEffects.WHITE_PHOSPHORUS, 600, 1, true, true));
        } else if (level == 2) {
            entity.addEffect(new MobEffectInstance(BMNWEffects.WHITE_PHOSPHORUS, 1200, 2, true, true));
        } else inflictWP(entity, 1);
    }
}
