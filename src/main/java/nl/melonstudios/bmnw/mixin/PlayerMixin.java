package nl.melonstudios.bmnw.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.interfaces.IScopeableItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "isScoping", at = @At("RETURN"), cancellable = true)
    public void isScoping(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() == Boolean.TRUE) return;
        Player player = (Player)(Object)this;
        ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (mainStack.getItem() instanceof IScopeableItem scope) {
            boolean b = scope.shouldScope(player, mainStack, InteractionHand.MAIN_HAND);
            if (b) {
                cir.setReturnValue(true);
                return;
            }
        }
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhandStack.getItem() instanceof IScopeableItem scope) {
            boolean b = scope.shouldScope(player, offhandStack, InteractionHand.OFF_HAND);
            if (b) {
                cir.setReturnValue(true);
            }
        }
        
    }
}
