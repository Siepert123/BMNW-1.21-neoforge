package nl.melonstudios.bmnw.item.weapons;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import nl.melonstudios.bmnw.init.BMNWDamageSources;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.IScopeableItem;
import nl.melonstudios.bmnw.misc.MiddleMouseButton;

public class SniperItem extends Item implements IScopeableItem {
    private static boolean wasSneaking = false;
    public SniperItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldScope(Player player, ItemStack stack, InteractionHand hand) {
        if (!wasSneaking && player.isShiftKeyDown()) {
            player.playSound(SoundEvents.SPYGLASS_USE);
            wasSneaking = true;
        } else if (wasSneaking && !player.isShiftKeyDown()) {
            player.playSound(SoundEvents.SPYGLASS_STOP_USING);
            wasSneaking = false;
        }
        return player.isShiftKeyDown();
    }

    @Override
    public float getFOVModifier(Player player, ItemStack stack, InteractionHand hand) {
        return 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand == InteractionHand.MAIN_HAND) {
            player.playSound(BMNWSounds.BOMB_1.get());
            EntityHitResult result = MiddleMouseButton.clickEntity(player, 2048, false);
            if (result != null) {
                Entity entity = result.getEntity();
                entity.hurt(BMNWDamageSources.shot(level), 20);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }
}
