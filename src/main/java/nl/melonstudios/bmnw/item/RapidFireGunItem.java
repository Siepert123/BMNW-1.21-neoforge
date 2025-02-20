package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.misc.GunOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class RapidFireGunItem extends GunItem {
    public RapidFireGunItem(Properties properties, GunOptions gunOptions) {
        super(properties, gunOptions);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (getAmmunition(stack) == 0) {
            stack.set(BMNWDataComponents.AMMUNITION, getMaxAmmunition(stack));
            return InteractionResultHolder.success(stack);
        } else {
            player.startUsingItem(usedHand);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getMaxAmmunition(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide()) {
            if (getAmmunition(stack) == 0) {
                if (livingEntity instanceof Player player) {
                    player.stopUsingItem();
                }
            } else {
                stack.set(BMNWDataComponents.AMMUNITION, getAmmunition(stack) - 1);
                shoot((ServerLevel) level, livingEntity, InteractionHand.MAIN_HAND, stack, List.of(new ItemStack(BMNWItems.LEAD_BULLET.get(), 8)),
                        5, 0.05f, true, null);
            }
            stack.set(BMNWDataComponents.IN_USE, true);
        }
        if (livingEntity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
}
