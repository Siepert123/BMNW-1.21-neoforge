package nl.melonstudios.bmnw.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import nl.melonstudios.bmnw.BMNW;

public class BuildersFurnaceScreen extends AbstractContainerScreen<BuildersFurnaceMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/builders_furnace.png");
    public BuildersFurnaceScreen(BuildersFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        ContainerData data = this.menu.data;

        int scaledArrow = this.scaledArrow(data, 24);
        int scaledFire = this.scaledFuel(data, 14);

        if (scaledArrow > 0) {
            graphics.blit(GUI_TEXTURE, x+79, y+34, 176, 14, scaledArrow, 17);
        }
        if (scaledFire > 0) {
            int offset = 14 - scaledFire;
            graphics.blit(GUI_TEXTURE, x+56, y+36+offset, 176, offset, 14, scaledFire);
        }
    }

    private int scaledArrow(ContainerData data, int w) {
        if (data.get(1) == 0) return 0;
        return data.get(0) * w / data.get(1);
    }
    private int scaledFuel(ContainerData data, int h) {
        if (data.get(3) == 0) return 0;
        return data.get(2) * h / data.get(3);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
