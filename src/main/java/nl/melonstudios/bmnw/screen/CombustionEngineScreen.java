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
import nl.melonstudios.bmnw.block.entity.CombustionEngineBlockEntity;
import nl.melonstudios.bmnw.item.battery.BatteryItem;

public class CombustionEngineScreen extends AbstractContainerScreen<CombustionEngineMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/combustion_engine/gui.png");
    public static final ResourceLocation FLAME_TEXTURE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    public CombustionEngineScreen(CombustionEngineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelX = 44;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        ItemStack battery = this.menu.be.inventory.getStackInSlot(CombustionEngineBlockEntity.SLOT_BATTERY);
        ItemStack water = this.menu.be.inventory.getStackInSlot(CombustionEngineBlockEntity.SLOT_WATER_BUCKET);
        ItemStack bucket = this.menu.be.inventory.getStackInSlot(CombustionEngineBlockEntity.SLOT_EMPTY_BUCKET);
        if (battery.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+26, y+60, 208, 60, 16, 16);
        }
        if (water.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+134, y+16, 176, 60, 16, 16);
        }
        if (bucket.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+134, y+60, 192, 60, 16, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int scaledFuel = this.menu.scaledFuel(14);
        if (scaledFuel > 0) {
            int offset = 14 - scaledFuel;
            graphics.blitSprite(FLAME_TEXTURE, 14, 14, 0, offset, x+81, y+32+offset, 14, scaledFuel);
        }
        int scaledEnergy = this.menu.scaledEnergy(60);
        if (scaledEnergy > 0) {
            int offset = 60 - scaledEnergy;
            graphics.blit(GUI_TEXTURE, x+8, y+16+offset, 192, offset, 8, scaledEnergy);
        }
        int scaledFluid = this.menu.scaledFluid(60);
        if (scaledFluid > 0) {
            int offset = 60 - scaledFluid;
            graphics.blit(GUI_TEXTURE,x+152, y+16+offset, 176, 0, 16, scaledFluid);
        }

        this.renderTooltip(graphics, mouseX, mouseY);
        if (this.menu.getCarried().isEmpty()) {
            int mx = mouseX - x;
            int my = mouseY - y;
            if (mx > 6 && my > 14 && mx < 17 && my < 77) {
                graphics.renderTooltip(
                        this.font,
                        Component.literal(BatteryItem.formatNicely(this.menu.data.get(2)) + "/" + BatteryItem.formatNicely(CombustionEngineBlockEntity.ENERGY_CAPACITY)),
                        mouseX, mouseY
                );
            }
            if (mx > 150 && my > 14 && mx < 169 && my < 77) {
                graphics.renderTooltip(
                        this.font,
                        Component.literal(this.menu.data.get(3) + "mB/" + CombustionEngineBlockEntity.WATER_CAPACITY + "mB"),
                        mouseX, mouseY
                );
            }
        }
    }
}
