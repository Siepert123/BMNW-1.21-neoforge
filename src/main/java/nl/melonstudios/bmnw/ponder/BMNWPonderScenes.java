package nl.melonstudios.bmnw.ponder;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.ponder.scene.EarlyGameScenes;
import nl.melonstudios.bmnw.ponder.scene.ExplosivesScenes;

public class BMNWPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<DeferredItem<?>> registration = helper.withKeyFunction(DeferredItem::getId);

        registration.addStoryBoard(BMNWItems.PRESS, "early_game/stamping_press",
                EarlyGameScenes::stampingPress, BMNWPonderTags.EARLY_GAME);
        registration.addStoryBoard(BMNWItems.ALLOY_BLAST_FURNACE, "early_game/alloy_blast_furnace",
                EarlyGameScenes::alloyBlastFurnace, BMNWPonderTags.EARLY_GAME);

        registration.addStoryBoard(BMNWItems.LITTLE_BOY, "explosives/little_boy",
                ExplosivesScenes::littleBoy, BMNWPonderTags.EXPLOSIVES);
        registration.addStoryBoard(BMNWItems.CASEOH, "explosives/caseoh",
                ExplosivesScenes::caseoh, BMNWPonderTags.EXPLOSIVES);
        registration.addStoryBoard(BMNWItems.TSAR_BOMBA, "explosives/tsar_bomba",
                ExplosivesScenes::tsarBomba, BMNWPonderTags.EXPLOSIVES);
    }
}
