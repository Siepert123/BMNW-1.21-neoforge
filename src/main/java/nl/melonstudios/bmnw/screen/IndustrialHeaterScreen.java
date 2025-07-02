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
import nl.melonstudios.bmnw.block.entity.IndustrialHeaterBlockEntity;
import nl.melonstudios.bmnw.item.battery.BatteryItem;
import nl.melonstudios.bmnw.misc.Library;

public class IndustrialHeaterScreen extends AbstractContainerScreen<IndustrialHeaterMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/industrial_heater.png");

    public IndustrialHeaterScreen(IndustrialHeaterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = 36;
        this.imageWidth = 176;
        this.imageHeight = 130;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        ItemStack fuel0 = this.menu.be.inventory.getStackInSlot(0);
        ItemStack fuel1 = this.menu.be.inventory.getStackInSlot(1);

        if (fuel0.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+26, y+17, 190, 0, 16, 16);
        }
        if (fuel1.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+44, y+17, 190, 0, 16, 16);
        }

        int scaledHeat = this.menu.scaledHeat(106);
        if (scaledHeat > 0) {
            graphics.blit(GUI_TEXTURE, x+62, y+17, 0, 130, scaledHeat, 8);
        }
        int scaledFuel = this.menu.scaledFuel();
        if (scaledFuel > 0) {
            int offset = 14 - scaledFuel;
            graphics.blit(GUI_TEXTURE, x+8, y+18+offset, 176, offset, 14, scaledFuel);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.renderTooltip(graphics, mouseX, mouseY);

        if (this.menu.getCarried().isEmpty()) {
            if (mouseX >= x+61 && mouseY >= y+16 && mouseX < x+169 && mouseY < y+26) {
                graphics.renderTooltip(this.font,
                        Component.literal(Library.formatThermalUnits(this.menu.data.get(0)) + "/" +
                                Library.formatThermalUnits(IndustrialHeaterBlockEntity.MAX_HEAT_STORAGE)), mouseX, mouseY);
            }
        }
    }
}
