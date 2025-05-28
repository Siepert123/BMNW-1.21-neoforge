package nl.melonstudios.bmnw.mixin;

import net.minecraft.client.gui.Gui;
import nl.melonstudios.bmnw.client.BMNWClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(CallbackInfo ci) {
        BMNWClient.onDisconnect();
    }
}
