package nl.melonstudios.bmnw.item.weapons;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import nl.melonstudios.bmnw.init.BMNWDamageSources;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.IHeldLikeCrossbow;
import nl.melonstudios.bmnw.interfaces.IScopeableItem;
import nl.melonstudios.bmnw.misc.MiddleMouseButton;

import java.awt.*;
import java.util.List;

public class SniperItem extends Item implements IScopeableItem, IHeldLikeCrossbow {
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
        if (usedHand == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean fire = canFire(stack);
            if (!fire) {
                if (tryRefill(player, stack)) {
                    player.playSound(BMNWSounds.DOOR_LOL_3.get());
                    player.getCooldowns().addCooldown(this, 20);
                    return InteractionResultHolder.success(stack);
                }
                return InteractionResultHolder.pass(stack);
            } else {
                player.playSound(BMNWSounds.GENERIC_GUNSHOT.get());
                EntityHitResult result = MiddleMouseButton.clickEntity(player, 2048, false);
                if (result != null) {
                    Entity entity = result.getEntity();
                    entity.hurt(BMNWDamageSources.shot(level), 20);
                }
                player.xRotO = player.getXRot();
                player.setXRot(player.getXRot() - 1);
                stack.set(BMNWDataComponents.AMMUNITION.get(), 0);
                player.getCooldowns().addCooldown(this, 20);
                return InteractionResultHolder.fail(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    public static boolean canFire(ItemStack stack) {
        if (!stack.has(BMNWDataComponents.AMMUNITION.get())) {
            stack.set(BMNWDataComponents.AMMUNITION.get(), 0);
            return false;
        }
        int ammo = stack.get(BMNWDataComponents.AMMUNITION.get());
        return ammo > 0;
    }
    public static boolean tryRefill(Player player, ItemStack stack) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack is = inventory.items.get(i);
            if (is.is(BMNWItems.ADVANCED_CIRCUIT.get())) {
                is.shrink(1);
                stack.set(BMNWDataComponents.AMMUNITION.get(), 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return canFire(stack) ? 13 : 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return canFire(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Color.HSBtoRGB(0.333F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.bmnw.ammo").append(": ").append(canFire(stack) ? "1" : "0").append("/1"));
    }
}
