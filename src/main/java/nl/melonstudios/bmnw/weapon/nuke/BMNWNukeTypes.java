package nl.melonstudios.bmnw.weapon.nuke;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.weapon.nuke.type.CaseohNukeType;
import nl.melonstudios.bmnw.weapon.nuke.type.LittleBoyNukeType;

public class BMNWNukeTypes {
    private static final DeferredRegister<NukeType> NUKES =
            DeferredRegister.create(BMNWResourceKeys.NUKE_TYPE_REGISTRY, "bmnw");

    public static final DeferredHolder<NukeType, LittleBoyNukeType> LITTLE_BOY = NUKES.register(
            "little_boy", LittleBoyNukeType::new
    );
    public static final DeferredHolder<NukeType, CaseohNukeType> CASEOH = NUKES.register(
            "caseoh", CaseohNukeType::new
    );

    public static void register(IEventBus eventBus) {
        NUKES.register(eventBus);
    }
}
