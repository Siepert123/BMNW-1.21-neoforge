package nl.melonstudios.bmnw.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.misc.FluidTextureData;
import nl.melonstudios.bmnw.misc.Library;
import nl.melonstudios.bmnw.wifi.PacketFluidIdentifier;

import java.util.ArrayList;
import java.util.function.Predicate;

public class FluidIdentifierSelectionScreen extends Screen {
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/fluid_identifier_selection.png");

    private static final int COLUMNS = 5;
    private static final int ROWS = 10;

    private final ItemStack identifier;
    private final Player player;
    private int selection;
    private int page;
    private String googleSearch = "";
    private final ArrayList<Fluid> options;

    private final MouseArea prevPageArea = new MouseArea(4, 195, 18, 18);
    private final MouseArea nextPageArea = new MouseArea(76, 195, 18, 18);
    private final MouseArea doneArea = new MouseArea(40, 195, 18, 18);
    private final MouseArea[] fluidAreas = new MouseArea[COLUMNS*ROWS];

    public FluidIdentifierSelectionScreen(ItemStack identifier, Player player) {
        super(Component.translatable("screen.bmnw.fluid_identifier"));

        this.identifier = identifier;
        this.player = player;

        this.selection = -1;
        this.page = 0;
        this.options = new ArrayList<>();
        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < ROWS; row++) {
                fluidAreas[column+row*5] = new MouseArea(5+column*18, 13+row*18, 16, 16);
            }
        }

        this.gatherFluids(Library.ALWAYS_TRUE);
    }

    private void gatherFluids(Predicate<? super Fluid> filter) {
        this.options.clear();
        for (Fluid fluid : Library.wrapIterator(BuiltInRegistries.FLUID.iterator())) {
            if (fluid.isSource(fluid.defaultFluidState()) && filter.test(fluid)) this.options.add(fluid);
        }
        this.options.trimToSize();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        if (this.player.getItemInHand(InteractionHand.MAIN_HAND) != this.identifier) this.getMinecraft().setScreen(null);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        int x = (this.width - 98) / 2;
        int y = (this.height - 226) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, 109, 226);

        graphics.drawCenteredString(this.font, this.getTitle(), x+49, y+2, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.literal(this.googleSearch), x+49, y+216, 0xFFFFFFFF);

        for (int i = this.getStartIdx(); i < Math.min(this.options.size(), this.getStartIdx() + COLUMNS*ROWS); i++) {
            FluidTextureData data = FluidTextureData.getStillTexture(this.options.get(i));
            int color = data.extensions().getTintColor();
            float r = FastColor.ARGB32.red(color) / 255.0F;
            float g = FastColor.ARGB32.green(color) / 255.0F;
            float b = FastColor.ARGB32.blue(color) / 255.0F;
            float a = FastColor.ARGB32.alpha(color) / 255.0F;
            graphics.blit(x+5+(i%COLUMNS)*18, y+13+(i/COLUMNS)*18, 0, 16, 16,
                    data.material().sprite(), r, g, b, a);
        }

        if (this.selection >= this.getStartIdx() && this.selection < Math.min(this.options.size(), this.getStartIdx() + COLUMNS*ROWS)) {
            graphics.blit(GUI_TEXTURE, x+3+(this.selection%COLUMNS)*18, y+11+(this.selection/COLUMNS)*18,
                    0, 226, 20, 20);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = (this.width - 98) / 2;
        int y = (this.height - 226) / 2;

        int mx = mouseX-x;
        int my = mouseY-y;

        if (this.prevPageArea.isInArea(mx, my)) {
            graphics.renderTooltip(this.font, Component.translatable("screen.bmnw.previousPage"), mouseX, mouseY);
        } else if (this.nextPageArea.isInArea(mx, my)) {
            graphics.renderTooltip(this.font, Component.translatable("screen.bmnw.nextPage"), mouseX, mouseY);
        } else if (this.doneArea.isInArea(mx, my)) {
            graphics.renderTooltip(this.font, Component.translatable("screen.bmnw.apply"), mouseX, mouseY);
        } else {
            for (int i = 0; i < this.fluidAreas.length; i++) {
                if (this.fluidAreas[i].isInArea(mx, my)) {
                    if (this.getStartIdx()+i < this.options.size()) {
                        graphics.renderTooltip(this.font, this.options.get(this.getStartIdx()+i).getFluidType().getDescription(),
                                mouseX, mouseY);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        int x = (this.width - 98) / 2;
        int y = (this.height - 226) / 2;

        int mx = (int) (mouseX-x);
        int my = (int) (mouseY-y);

        if (this.prevPageArea.isInArea(mx, my)) {
            if (this.tryPreviousPage()) {
                BMNWSounds.playClick();
                return true;
            }
        } else if (this.nextPageArea.isInArea(mx, my)) {
            if (this.tryNextPage()) {
                BMNWSounds.playClick();
                return true;
            }
        } else if (this.doneArea.isInArea(mx, my)) {
            BMNWSounds.playClick();
            if (this.selection != -1) {
                String fluidID = BuiltInRegistries.FLUID.getKey(this.options.get(this.selection)).toString();
                this.identifier.set(BMNWDataComponents.FLUID_TYPE, fluidID);
                PacketDistributor.sendToServer(new PacketFluidIdentifier(fluidID));
            }
            this.onClose();
            return true;
        }else {
            for (int i = 0; i < this.fluidAreas.length; i++) {
                if (this.fluidAreas[i].isInArea(mx, my)) {
                    if (this.getStartIdx()+i < this.options.size()) {
                        this.selection = this.getStartIdx()+i;
                        BMNWSounds.playClick();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.googleSearch.length() < 16) {
            this.selection = -1;
            this.googleSearch += codePoint;
            this.gatherFluids((fluid) -> fluid.getFluidType().getDescription().getString().toLowerCase()
                    .contains(this.googleSearch.toLowerCase()));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.onClose();
            return true;
        }
        if (keyCode == 259 && !this.googleSearch.isEmpty()) {
            this.selection = -1;
            this.googleSearch = this.googleSearch.substring(0, this.googleSearch.length()-1);
            if (this.googleSearch.isEmpty()) {
                this.gatherFluids(Library.ALWAYS_TRUE);
            } else {
                this.gatherFluids((fluid) -> fluid.getFluidType().getDescription().getString().toLowerCase()
                        .contains(this.googleSearch.toLowerCase()));
            }
            return true;
        }
        return false;
    }

    private int getStartIdx() {
        return this.page * COLUMNS * ROWS;
    }
    private int calculatePages() {
        return this.options.size() / (COLUMNS * ROWS);
    }

    private boolean tryPreviousPage() {
        if (this.page == 0) return false;
        this.page--;
        return true;
    }
    private boolean tryNextPage() {
        if (this.page < this.calculatePages()) {
            this.page++;
            return true;
        }
        return false;
    }
}
