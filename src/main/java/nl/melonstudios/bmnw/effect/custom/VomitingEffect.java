package nl.melonstudios.bmnw.effect.custom;

import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class VomitingEffect extends MobEffect {
    public VomitingEffect() {
        super(MobEffectCategory.HARMFUL, 0x005500);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 look = entity.getLookAngle().multiply(2, 2, 2);
        RandomSource rand = entity.getRandom();
        for (int i = 0; i < 10; i++) {
            Vec3 vec = look.xRot(rand.nextFloat() - 0.5f).yRot(rand.nextFloat() - 0.5f).zRot(rand.nextFloat() - 0.5f);
            entity.level().addParticle(BMNWParticleTypes.VOMIT.get(),
                    entity.getX(), entity.getEyeY(), entity.getZ(),
                    vec.x(), vec.y(), vec.z());
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {

    }
}
