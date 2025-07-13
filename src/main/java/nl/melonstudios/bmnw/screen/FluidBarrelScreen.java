package nl.melonstudios.bmnw.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.misc.FluidTextureData;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.wifi.PacketCycleFluidBarrelConfig;

public class FluidBarrelScreen extends AbstractContainerScreen<FluidBarrelMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/fluid_tank_generic.png");
    public FluidBarrelScreen(FluidBarrelMenu menu, Inventory playerInventory, Component title) {
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

        ItemStack fillIn = this.menu.be.inventory.getStackInSlot(0);
        ItemStack fillOut = this.menu.be.inventory.getStackInSlot(1);
        ItemStack drainIn = this.menu.be.inventory.getStackInSlot(2);
        ItemStack drainOut = this.menu.be.inventory.getStackInSlot(3);

        if (fillIn.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+35, y+17, 176, 0, 16, 16);
        }
        if (fillOut.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+35, y+65, 192, 0, 16, 16);
        }
        if (drainIn.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+125, y+17, 192, 0, 16, 16);
        }
        if (drainOut.isEmpty()) {
            graphics.blit(GUI_TEXTURE, x+125, y+65, 176, 0, 16, 16);
        }

        if (!this.menu.be.fluidType.isSame(Fluids.EMPTY)) {
            int max = this.menu.be.tank.getCapacity();
            int fill = this.menu.be.tank.getFluidAmount() * 64 / max;
            if (fill > 0) {
                int reverse = 64 - fill;
                FluidStack stack = this.menu.be.tank.getFluid();
                FluidTextureData data = FluidTextureData.getStillTexture(stack.getFluid());
                int color = data.extensions().getTintColor(stack);
                graphics.blit(x + 62, y + 17 + reverse, 0, 52, 64 - reverse, data.material().sprite(),
                        FastColor.ARGB32.red(color) / 255.0F,
                        FastColor.ARGB32.green(color) / 255.0F,
                        FastColor.ARGB32.blue(color) / 255.0F,
                        FastColor.ARGB32.alpha(color) / 255.0F
                );
            }
        }

        graphics.blit(GUI_TEXTURE, x+62, y+17, 176, 16, 52, 64);

        graphics.blit(GUI_TEXTURE, x+155, y+26,
                this.getOffset(176, this.menu.be.flowRedstoneOn.in),
                this.getOffset(80, this.menu.be.flowRedstoneOn.out),
                16, 16);
        graphics.blit(GUI_TEXTURE, x+155, y+56,
                this.getOffset(176, this.menu.be.flowRedstoneOff.in),
                this.getOffset(80, this.menu.be.flowRedstoneOff.out),
                16, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int mx = mouseX-x;
        int my = mouseY-y;
        if (this.menu.getCarried().isEmpty()) {
            if (mx >= 61 && my >= 16 && mx < 61+54 && my < 16+66) {
                guiGraphics.renderTooltip(this.font,
                        this.menu.be.fluidType.getFluidType().getDescription().plainCopy()
                                .append(": " + Library.formatMilliBuckets(this.menu.be.tank.getFluidAmount())
                                        + "/" + Library.formatMilliBuckets(this.menu.be.tank.getCapacity())),
                        mouseX, mouseY);
                return;
            }
            if (this.tryClickFlowRedstoneOn(mx, my, true)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal("Flow type: " + this.menu.be.flowRedstoneOn.name()),
                        mouseX, mouseY);
                return;
            }
            if (this.tryClickFlowRedstoneOff(mx, my, true)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal("Flow type: " + this.menu.be.flowRedstoneOff.name()),
                        mouseX, mouseY);
                return;
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
        if (this.tryClickFlowRedstoneOff(mx, my, false)) return true;

        return false;
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
