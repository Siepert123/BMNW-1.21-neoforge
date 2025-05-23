package nl.melonstudios.bmnw.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import nl.melonstudios.bmnw.BMNW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Unique private final SplashRenderer memz_splash = new SplashRenderer(BMNW.randomSplash);
    @Shadow @Nullable private SplashRenderer splash;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (BMNW.memz) {
            splash = null;
            TitleScreen screen = (TitleScreen)(Object)this;
            memz_splash.render(guiGraphics, screen.width, screen.getMinecraft().font,
                    Color.HSBtoRGB((System.currentTimeMillis() % 10000) / 10000.0F, 1.0F, 1.0F));
        }
    }
}
