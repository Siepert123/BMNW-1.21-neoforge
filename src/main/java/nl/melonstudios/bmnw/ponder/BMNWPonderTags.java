package nl.melonstudios.bmnw.ponder;

import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWItems;

public class BMNWPonderTags {

    public static final ResourceLocation EARLY_GAME = BMNW.namespace("early_game");
    public static final ResourceLocation EXPLOSIVES = BMNW.namespace("explosives");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<DeferredItem<?>> registration = helper.withKeyFunction(DeferredHolder::getId);

        registration.registerTag(EARLY_GAME)
                .addToIndex()
                .item(BMNWItems.ALLOY_BLAST_FURNACE, true, false)
                .title("Early Game Technology")
                .description("Machines used in the early game phase")
                .register();
        registration.addToTag(EARLY_GAME)
                .add(BMNWItems.PRESS)
                .add(BMNWItems.ALLOY_BLAST_FURNACE);

        registration.registerTag(EXPLOSIVES)
                .addToIndex()
                .item(BMNWItems.NUCLEAR_CHARGE, true, false)
                .title("Extreme Explosives")
                .description("Various extreme explosives for death and destruction")
                .register();
        registration.addToTag(EXPLOSIVES)
                .add(BMNWItems.DROPPED_SOULFIRE_BOMB)
                .add(BMNWItems.NUCLEAR_CHARGE)
                .add(BMNWItems.LITTLE_BOY)
                .add(BMNWItems.CASEOH)
                .add(BMNWItems.TSAR_BOMBA);

    }
}
