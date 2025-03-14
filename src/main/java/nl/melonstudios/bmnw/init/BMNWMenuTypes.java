package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.screen.AlloyFurnaceMenu;
import nl.melonstudios.bmnw.screen.PressMenu;
import nl.melonstudios.bmnw.screen.WorkbenchMenu;

public class BMNWMenuTypes {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, "bmnw");

    public static final DeferredHolder<MenuType<?>, MenuType<PressMenu>> PRESS = MENU_TYPES.register(
            "press",
            () -> IMenuTypeExtension.create(PressMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<AlloyFurnaceMenu>> ALLOY_BLAST_FURNACE = MENU_TYPES.register(
            "alloy_blast_furnace",
            () -> IMenuTypeExtension.create(AlloyFurnaceMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>> WORKBENCH = MENU_TYPES.register(
            "workbench",
            () -> IMenuTypeExtension.create(WorkbenchMenu::new)
    );

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
