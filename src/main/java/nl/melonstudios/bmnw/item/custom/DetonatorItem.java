package nl.melonstudios.bmnw.item.custom;

import nl.melonstudios.bmnw.init.BMNWAdvancementTriggers;
import nl.melonstudios.bmnw.interfaces.IDetonatable;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.init.BMNWTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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

        if (stack.has(BMNWDataComponents.TARGET)) {
            BlockPos target = stack.get(BMNWDataComponents.TARGET);

            if (target != null && level.getBlockState(target).getBlock() instanceof IDetonatable) {
                if (level.getBlockState(target).is(BMNWTags.Blocks.GRANTS_NUKE_ACHIEVEMENT) && !level.isClientSide()) {
                    BMNWAdvancementTriggers.NUKE.get().trigger((ServerPlayer) player);
                }
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
                context.getItemInHand().set(BMNWDataComponents.TARGET, context.getClickedPos());
                if (context.getLevel().isClientSide()) context.getPlayer().sendSystemMessage(Component.translatable("text.bmnw.position_set").withColor(0x00DD00));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
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
