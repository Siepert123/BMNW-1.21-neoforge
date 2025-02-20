package nl.melonstudios.bmnw.init;

import nl.melonstudios.bmnw.effect.custom.ContaminationEffect;
import nl.melonstudios.bmnw.effect.custom.VomitingEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.effect.custom.WPEffect;

public class BMNWEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "bmnw");

    public static final Holder<MobEffect> CONTAMINATION = MOB_EFFECTS.register("contamination", ContaminationEffect::new);
    public static final Holder<MobEffect> VOMITING = MOB_EFFECTS.register("vomiting", VomitingEffect::new);
    public static final Holder<MobEffect> WHITE_PHOSPHORUS = MOB_EFFECTS.register("white_phosphorus", WPEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
