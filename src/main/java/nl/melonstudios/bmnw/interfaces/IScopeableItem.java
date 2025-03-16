package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IScopeableItem {
    boolean shouldScope(Player player, ItemStack stack, InteractionHand hand);
    float getFOVModifier(Player player, ItemStack stack, InteractionHand hand);
}
