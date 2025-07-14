package nl.melonstudios.bmnw.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlockEntity;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.wifi.PacketCycleFluidBarrelConfig;

public class EnergyStorageScreen extends AbstractContainerScreen<EnergyStorageMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/energy_storage_generic.png");
    public EnergyStorageScreen(EnergyStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 178;
        this.imageWidth = 176;

        this.inventoryLabelY = 84;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        EnergyStorageBlockEntity be = this.menu.be;
        ItemStack batteryIn = be.inventory.getStackInSlot(0);
        ItemStack batteryOut = be.inventory.getStackInSlot(1);

        boolean cycle = (System.currentTimeMillis() / 1000 & 1) == 0;

        if (batteryIn.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x + 35, y + 17, 176 + (cycle ? 16 : 0), 0, 16, 16);
        }
        if (batteryOut.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x + 35, y + 65, 176 + (cycle ? 0 : 16), 0, 16, 16);
        }

        if (this.menu.energyTracker.get() > 0) {
            int max = this.menu.block.capacity;
            int fill = this.menu.energyTracker.get() * 64 / max;
            if (fill > 0) {
                int reverse = 64 - fill;
                graphics.blit(GUI_TEXTURE, x+62, y+17+reverse, 176, 16+reverse, 52, fill);
            }
        }

        graphics.blit(GUI_TEXTURE, x+155, y+26,
                this.getOffset(176, be.flowRedstoneOn.in),
                this.getOffset(80, be.flowRedstoneOn.out),
                16, 16);
        graphics.blit(GUI_TEXTURE, x+155, y+56,
                this.getOffset(176, be.flowRedstoneOff.in),
                this.getOffset(80, be.flowRedstoneOff.out),
                16, 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        this.renderTooltip(graphics, mouseX, mouseY);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int mx = mouseX-x;
        int my = mouseY-y;
        if (this.menu.getCarried().isEmpty()) {
            if (mx >= 61 && my >= 16 && mx < 61+54 && my < 16+66) {
                graphics.renderTooltip(this.font,
                        Component.literal(Library.formatRedstoneFlux(this.menu.energyTracker.get())
                                + "/" +
                                Library.formatRedstoneFlux(this.menu.block.capacity)),
                        mouseX, mouseY);
                return;
            }
            if (this.tryClickFlowRedstoneOn(mx, my, true)) {
                graphics.renderTooltip(this.font,
                        Component.literal("Flow type: " + this.menu.be.flowRedstoneOn.name()),
                        mouseX, mouseY);
                return;
            }
            if (this.tryClickFlowRedstoneOff(mx, my, true)) {
                graphics.renderTooltip(this.font,
                        Component.literal("Flow type: " + this.menu.be.flowRedstoneOff.name()),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (!this.menu.getCarried().isEmpty() || button != 0) return true;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int mx = (int) (mouseX - x);
        int my = (int) (mouseY - y);

        if (this.tryClickFlowRedstoneOn(mx, my, false)) return true;
        return this.tryClickFlowRedstoneOff(mx, my, false);
    }

    private boolean tryClickFlowRedstoneOn(int mx, int my, boolean checkOnly) {
        if (mx >= 154 && my >= 25 && mx < 154+18 && my < 25+18) {
            if (checkOnly) return true;
            BMNWSounds.playClick();
            PacketDistributor.sendToServer(new PacketCycleFluidBarrelConfig(this.menu.be.getBlockPos(), true));
            return true;
        }
        return false;
    }
    private boolean tryClickFlowRedstoneOff(int mx, int my, boolean checkOnly) {
        if (mx >= 154 && my >= 55 && mx < 154+18 && my < 55+18) {
            if (checkOnly) return true;
            BMNWSounds.playClick();
            PacketDistributor.sendToServer(new PacketCycleFluidBarrelConfig(this.menu.be.getBlockPos(), false));
            return true;
        }
        return false;
    }

    private int getOffset(int o, boolean b) {
        return b ? o : o+16;
    }
}
