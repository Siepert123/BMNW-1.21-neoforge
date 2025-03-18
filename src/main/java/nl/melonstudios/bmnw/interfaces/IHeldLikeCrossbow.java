package nl.melonstudios.bmnw.interfaces;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;

public interface IHeldLikeCrossbow {
    default boolean shouldHoldLikeCrossbow(AbstractClientPlayer player, InteractionHand hand) {
        return true;
    }
}
