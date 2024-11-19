package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.item.components.ModDataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class TargetDesignatorItem extends Item {
    public TargetDesignatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.MISSILE_LAUNCH_PAD)) {
            MissileLaunchPadBlockEntity be = (MissileLaunchPadBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (be != null) {
                ItemStack stack = context.getItemInHand();
                if (stack.has(ModDataComponents.TARGET)) {
                    be.setTarget(stack.get(ModDataComponents.TARGET));
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            ItemStack stack = context.getItemInHand();
            stack.set(ModDataComponents.TARGET, context.getClickedPos());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
