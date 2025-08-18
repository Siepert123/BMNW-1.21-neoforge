package nl.melonstudios.bmnw.screen.nuke;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.weapon.nuke.block.LittleBoyBE;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LittleBoyScreen extends AbstractContainerScreen<LittleBoyMenu> {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/nuke/little_boy.png");

    public LittleBoyScreen(LittleBoyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 191;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        graphics.setColor(1.0F, 1.0F, 1.0F, 0.5F);
        graphics.renderItem(LittleBoyBE.PATTERN[0].asItem().getDefaultInstance(), x+35, y+72);
        graphics.renderItem(LittleBoyBE.PATTERN[1].asItem().getDefaultInstance(), x+53, y+72);
        graphics.renderItem(LittleBoyBE.PATTERN[2].asItem().getDefaultInstance(), x+71, y+72);
        graphics.renderItem(LittleBoyBE.PATTERN[3].asItem().getDefaultInstance(), x+89, y+72);
        graphics.renderItem(LittleBoyBE.PATTERN[4].asItem().getDefaultInstance(), x+107, y+72);
        graphics.renderItem(LittleBoyBE.PATTERN[5].asItem().getDefaultInstance(), x+125, y+72);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.menu.be.compareInventoryToPattern()) {
            graphics.blit(GUI_TEXTURE, x+152, y+72, 16, 191, 16, 16);
        } else {
            graphics.blit(GUI_TEXTURE, x+152, y+72, 0, 191, 16, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.renderTooltip(graphics, mouseX, mouseY);

        if (this.menu.getCarried().isEmpty()) {
            if (mouseX >= x + 151 && mouseY >= y + 71 && mouseX < x + 169 && mouseY < y + 89) {
                graphics.renderComponentTooltip(this.font, this.menu.be.collectInformation(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}
}
