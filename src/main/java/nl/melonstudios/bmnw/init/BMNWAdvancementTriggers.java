package nl.melonstudios.bmnw.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.critereon.NukeCriterionTrigger;

import java.util.function.Supplier;

public class BMNWAdvancementTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, "bmnw");

    public static final Supplier<NukeCriterionTrigger> NUKE =
            TRIGGERS.register("nuke", NukeCriterionTrigger::new);

    public static void register(IEventBus eventBus) {
        TRIGGERS.register(eventBus);
    }
}
