package nl.melonstudios.bmnw.weapon;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;

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
        if (stack.has(BMNWDataComponents.TARGET)) {
            BlockPos target = stack.get(BMNWDataComponents.TARGET);
            if (target == null) {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.literal("?"));
                }
                return InteractionResultHolder.fail(stack);
            }
            if (level.getBlockState(target).getBlock() instanceof RemoteActivateable activateable) {
                if (activateable.onRemoteActivation(level, target)) {
                    if (!level.isClientSide) {
                        player.sendSystemMessage(Component.literal("Activation successful!"));
                    }
                    return InteractionResultHolder.success(stack);
                } else {
                    if (!level.isClientSide) {
                        player.sendSystemMessage(Component.literal("Activation failed..."));
                    }
                }
            } else {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.literal("The targeted block cannot be remotely activated"));
                }
            }
        } else {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("Please select a target first!"));
            }
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            context.getItemInHand().set(BMNWDataComponents.TARGET, context.getClickedPos());

            if (!context.getLevel().isClientSide && context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.literal("Target set!"));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
