package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.screen.*;
import nl.melonstudios.bmnw.screen.nuke.CaseohMenu;
import nl.melonstudios.bmnw.screen.nuke.LittleBoyMenu;

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
    public static final DeferredHolder<MenuType<?>, MenuType<BuildersFurnaceMenu>> BUILDERS_FURNACE = MENU_TYPES.register(
            "builders_furnace",
            () -> IMenuTypeExtension.create(BuildersFurnaceMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>> WORKBENCH = MENU_TYPES.register(
            "workbench",
            () -> IMenuTypeExtension.create(WorkbenchMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<CombustionEngineMenu>> COMBUSTION_ENGINE = MENU_TYPES.register(
            "combustion_engine",
            () -> IMenuTypeExtension.create(CombustionEngineMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<IndustrialHeaterMenu>> INDUSTRIAL_HEATER = MENU_TYPES.register(
            "industrial_heater",
            () -> IMenuTypeExtension.create(IndustrialHeaterMenu::new)
    );

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyStorageMenu>> ENERGY_STORAGE = MENU_TYPES.register(
            "energy_storage",
            () -> IMenuTypeExtension.create(EnergyStorageMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<FluidBarrelMenu>> FLUID_BARREL = MENU_TYPES.register(
            "fluid_barrel",
            () -> IMenuTypeExtension.create(FluidBarrelMenu::new)
    );

    //region NUKES!!

    public static final DeferredHolder<MenuType<?>, MenuType<LittleBoyMenu>> LITTLE_BOY = MENU_TYPES.register(
            "little_boy",
            () -> IMenuTypeExtension.create(LittleBoyMenu::new)
    );
    public static final DeferredHolder<MenuType<?>, MenuType<CaseohMenu>> CASEOH = MENU_TYPES.register(
            "caseoh",
            () -> IMenuTypeExtension.create(CaseohMenu::new)
    );

    //endregion

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
