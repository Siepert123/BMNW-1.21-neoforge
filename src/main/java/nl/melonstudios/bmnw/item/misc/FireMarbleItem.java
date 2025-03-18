package nl.melonstudios.bmnw.item.misc;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.init.BMNWDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireMarbleItem extends Item {
    public FireMarbleItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.isInWaterRainOrBubble() && getChargeNonNull(stack) > 0.0F) {
            stack.set(BMNWDataComponents.FIRE_MARBLE_CHARGE, 0.0F);
            entity.playSound(SoundEvents.FIRE_EXTINGUISH);
        }
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getChargeNonNull(stack) > 0.0F;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(getChargeNonNull(stack) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) (getChargeNonNull(stack) * 13.0F);
    }

    public static float getChargeNonNull(ItemStack stack) {
        if (!stack.has(BMNWDataComponents.FIRE_MARBLE_CHARGE)) {
            stack.set(BMNWDataComponents.FIRE_MARBLE_CHARGE, 0.0F);
            return 0.0F;
        }
        return stack.get(BMNWDataComponents.FIRE_MARBLE_CHARGE);
    }
}
