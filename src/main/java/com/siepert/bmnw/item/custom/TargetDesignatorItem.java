package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.item.components.BMNWDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TargetDesignatorItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    public TargetDesignatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) {
            MissileLaunchPadBlockEntity be = (MissileLaunchPadBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (be != null) {
                ItemStack stack = context.getItemInHand();
                if (stack.has(BMNWDataComponents.TARGET)) {
                    be.setTarget(stack.get(BMNWDataComponents.TARGET));
                    if (context.getLevel().isClientSide()) context.getPlayer().sendSystemMessage(Component.translatable("text.bmnw.data_set").withColor(0x00DD00));
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            ItemStack stack = context.getItemInHand();
            stack.set(BMNWDataComponents.TARGET, context.getClickedPos());
            if (context.getLevel().isClientSide()) context.getPlayer().sendSystemMessage(Component.translatable("text.bmnw.position_set").withColor(0x00DD00));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.has(BMNWDataComponents.TARGET)) {
            BlockPos target = stack.get(BMNWDataComponents.TARGET);
            if (target == null) {
                tooltipComponents.add(Component.translatable("text.bmnw.position_invalid").withColor(0xDD0000));
            } else {
                tooltipComponents.add(Component.literal(String.format("X: %s Y: %s Z: %s",
                        target.getX(), target.getY(), target.getZ())).withColor(0xAAAAAA));
            }
        } else {
            tooltipComponents.add(Component.translatable("text.bmnw.position_missing").withColor(0xDD0000));
        }
    }
}
