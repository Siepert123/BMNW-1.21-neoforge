package nl.melonstudios.bmnw.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.network.chat.Component;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.cfg.BMNWClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {

    @Inject(method = "render", at = @At("RETURN"))
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (BMNWClientConfig.disableHints()) return;
        LevelLoadingScreen screen = (LevelLoadingScreen)(Object)this;
        int middle = screen.width / 2;
        String rawTip = Component.translatable(BMNW.getRandomHint()).getString();
        String[] lines = rawTip.split("\\$");
        for (int i = 0; i < lines.length; i++) {
            guiGraphics.drawCenteredString(
                    screen.getMinecraft().font,
                    lines[i],
                    middle, 4 + i*8,
                    0xFFFFFFFF);
        }
    }
}
