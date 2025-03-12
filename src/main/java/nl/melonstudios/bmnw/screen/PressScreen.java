package nl.melonstudios.bmnw.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.entity.PressBlockEntity;

public class PressScreen extends AbstractContainerScreen<PressMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/press/gui.png");
    public PressScreen(PressMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        PressBlockEntity be = this.menu.blockEntity;
        if (be.fuel >= 1) {
            guiGraphics.blit(GUI_TEXTURE, x+34, y+54, 176, 0, 14, 14);
        }
        int scaledProgress = this.menu.scaledProgress(16);
        if (scaledProgress > 0) {
            guiGraphics.blit(GUI_TEXTURE, x+55, y+35, 176, 14, 18, this.menu.scaledProgress(16));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
