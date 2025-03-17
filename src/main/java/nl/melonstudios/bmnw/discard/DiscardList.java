package nl.melonstudios.bmnw.discard;

import net.minecraft.world.item.Item;
import nl.melonstudios.bmnw.init.BMNWItems;

import java.util.ArrayList;
import java.util.List;

//Basically a deprecation list to notify in game users
public class DiscardList {
    public static final List<Item> toDiscard = new ArrayList<>();

    static {
        toDiscard.add(BMNWItems.FRACTURIZER.asItem());
        toDiscard.add(BMNWItems.VOLCANIC_FRACTURIZER.asItem());
        toDiscard.add(BMNWItems.STRONG_FRACTURIZER.asItem());
        toDiscard.add(BMNWItems.STRONG_VOLCANIC_FRACTURIZER.asItem());

        toDiscard.add(BMNWItems.DUD.asItem());
        toDiscard.add(BMNWItems.NUCLEAR_CHARGE.asItem());
        toDiscard.add(BMNWItems.LITTLE_BOY.asItem());
        toDiscard.add(BMNWItems.CASEOH.asItem());

        toDiscard.add(BMNWItems.BASE_MISSILE.asItem());
        toDiscard.add(BMNWItems.EXAMPLE_MISSILE.asItem());
        toDiscard.add(BMNWItems.HE_MISSILE.asItem());
        toDiscard.add(BMNWItems.NUCLEAR_MISSILE.asItem());
        toDiscard.add(BMNWItems.ANTI_MISSILE_MISSILE.asItem());
    }
}
