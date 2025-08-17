package nl.melonstudios.bmnw.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.world.entity.player.Player;
import nl.melonstudios.bmnw.init.BMNWAdvancementTriggers;

import java.util.Optional;

public record NukeTriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<NukeTriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(NukeTriggerInstance::player)
    ).apply(instance, NukeTriggerInstance::new));

    public static Criterion<NukeTriggerInstance> instance(ContextAwarePredicate player) {
        return BMNWAdvancementTriggers.NUKE.get().createCriterion(new NukeTriggerInstance(Optional.of(player)));
    }

    public boolean matches(Player player) {
        return true;
    }
}
