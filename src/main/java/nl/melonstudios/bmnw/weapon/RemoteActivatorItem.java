package nl.melonstudios.bmnw.weapon;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RemoteActivatorItem extends Item {
    public RemoteActivatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isSecondaryUseActive()) return InteractionResultHolder.pass(stack);
        if (usedHand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 1.0F);
        }
        if (stack.has(BMNWDataComponents.TARGET)) {
            BlockPos target = stack.get(BMNWDataComponents.TARGET);
            if (target == null) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("?"), true);
                }
                return InteractionResultHolder.fail(stack);
            }
            if (level.getBlockState(target).getBlock() instanceof RemoteActivateable activateable) {
                if (activateable.onRemoteActivation(level, target)) {
                    if (!level.isClientSide) {
                        player.displayClientMessage(Component.literal("Activation successful!"), true);
                    }
                    return InteractionResultHolder.success(stack);
                } else {
                    if (!level.isClientSide) {
                        player.displayClientMessage(Component.literal("Activation failed..."), true);
                    }
                }
            } else {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("The targeted block cannot be remotely activated"), true);
                }
            }
        } else {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Please select a target first!"), true);
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            context.getItemInHand().set(BMNWDataComponents.TARGET, context.getClickedPos());

            if (context.getPlayer() != null) {
                if (context.getLevel().isClientSide) {
                    context.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 0.5F);
                }
                if (!context.getLevel().isClientSide) {
                    context.getPlayer().displayClientMessage(Component.literal("Target set!"), true);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!stack.has(BMNWDataComponents.TARGET)) return;
        BlockPos target = stack.get(BMNWDataComponents.TARGET);
        if (target == null) return;
        tooltipComponents.add(
                Component.literal("Linked to [" + target.getX() + "," + target.getY() + "," + target.getZ() + "]")
                        .withColor(0x888888)
        );
    }
}
