package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.fluid.VolcanicLavaFluid;

public class BMNWFluids {
    private static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, "bmnw");

    public static final DeferredHolder<Fluid, VolcanicLavaFluid.Source> VOLCANIC_LAVA = FLUIDS.register(
            "volcanic_lava",
            VolcanicLavaFluid.Source::new
    );
    public static final DeferredHolder<Fluid, VolcanicLavaFluid.Flowing> VOLCANIC_LAVA_FLOWING = FLUIDS.register(
            "volcanic_lava_flowing",
            VolcanicLavaFluid.Flowing::new
    );

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
