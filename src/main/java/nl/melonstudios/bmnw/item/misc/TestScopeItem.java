package nl.melonstudios.bmnw.item.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.interfaces.IScopeableItem;

public class TestScopeItem extends Item implements IScopeableItem {
    public TestScopeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldScope(Player player, ItemStack stack, InteractionHand hand) {
        return player.isShiftKeyDown();
    }

    @Override
    public float getFOVModifier(Player player, ItemStack stack, InteractionHand hand) {
        return 0.1f;
    }
}
