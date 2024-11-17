package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IDetonatable;
import com.siepert.bmnw.item.components.ModDataComponents;
import com.siepert.bmnw.misc.ModTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.DefaultBlockInteractionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DetonatorItem extends Item {
    public DetonatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (stack.has(ModDataComponents.TARGET)) {
            BlockPos target = stack.get(ModDataComponents.TARGET);

            if (target != null && level.getBlockState(target).getBlock() instanceof IDetonatable) {
                ((IDetonatable) level.getBlockState(target).getBlock()).detonate(level, target);
                if (level.isClientSide()) player.sendSystemMessage(Component.translatable("text.bmnw.detonate_success").withColor(0x00DD00));
                return InteractionResultHolder.success(stack);
            } else {
                if (level.isClientSide()) player.sendSystemMessage(Component.translatable("text.bmnw.detonate_fail").withColor(0xDD0000));
                return InteractionResultHolder.pass(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        if (context.getPlayer().isShiftKeyDown()) {
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof IDetonatable) {
                context.getItemInHand().set(ModDataComponents.TARGET, context.getClickedPos());
                if (context.getLevel().isClientSide()) context.getPlayer().sendSystemMessage(Component.translatable("text.bmnw.position_set").withColor(0x00DD00));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.has(ModDataComponents.TARGET)) {
            BlockPos target = stack.get(ModDataComponents.TARGET);
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
