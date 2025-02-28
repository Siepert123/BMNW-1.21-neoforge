package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.gui.menu.PressurizedPressMenu;

public class BMNWMenuTypes {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, "bmnw");

    public static final DeferredHolder<MenuType<?>, MenuType<PressurizedPressMenu>> PRESSURIZED_PRESS =
            MENU_TYPES.register("pressurized_press", () -> new MenuType<>(
                    PressurizedPressMenu::new,
                    FeatureFlagSet.of()
            ));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
