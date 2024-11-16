package com.siepert.bmnw.effect;

import com.siepert.bmnw.effect.custom.ContaminationEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "bmnw");

    public static final Holder<MobEffect> CONTAMINATION = MOB_EFFECTS.register("contamination",
            () -> new ContaminationEffect());

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
