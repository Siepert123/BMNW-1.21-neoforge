package com.melonstudios.bmnw.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class NukeCritereonTrigger extends SimpleCriterionTrigger<NukeTriggerInstance> {
    @Override
    public Codec<NukeTriggerInstance> codec() {
        return NukeTriggerInstance.CODEC;
    }
    public void trigger(ServerPlayer player) {
        this.trigger(player,
                nukeTriggerInstance -> nukeTriggerInstance.matches(player));
    }
}
