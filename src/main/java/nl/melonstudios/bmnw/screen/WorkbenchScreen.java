package nl.melonstudios.bmnw.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipes;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.wifi.PacketWorkbenchCraft;

import java.util.*;

public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {
    public static final RandomSource random = RandomSource.create();
    public static final ResourceLocation GUI_TEXTURE = BMNW.namespace("textures/gui/workbench/gui.png");

    protected final MouseArea scrollLeft, scrollRight, craft;
    protected final MouseArea[] craftingEntries = new MouseArea[10];
    protected final int itemNameX, itemNameY;
    protected final int ingredientListX, ingredientListY;

    protected int selectedRecipe = -1;
    protected int selectionScroll = 0;
    protected final int maxSelectionScroll;
    protected final int maxRecipeIdx;
    protected final List<WorkbenchRecipe> availableRecipes;

    public WorkbenchScreen(WorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.scrollLeft = new MouseArea(7, 24, 6, 36);
        this.scrollRight = new MouseArea(105, 24, 6, 36);
        this.craft =  new MouseArea(50, 61, 18, 18);

        for (int x = 0; x < 5; x++) {
            this.craftingEntries[x*2] = new MouseArea(15+x*19, 25, 16, 16);
            this.craftingEntries[x*2+1] = new MouseArea(15+x*19, 43, 16, 16);
        }

        this.titleLabelY = 4;

        this.itemNameX = 8;
        this.itemNameY = 14;

        this.ingredientListX = 113;
        this.ingredientListY = 14;

        this.availableRecipes = WorkbenchRecipes.instance.tierMap.getOrDefault(this.getMenu().tier, Collections.emptyList());
        this.maxSelectionScroll = Math.max(this.availableRecipes.size() / 2 - 5, 0);
        this.maxRecipeIdx = this.availableRecipes.size();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, "Inv.", this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        PoseStack poseStack = graphics.pose();

        if (!this.canScrollLeft()) {
            graphics.blit(GUI_TEXTURE, x+7, y+24, 176, 0, 6, 36);
        }
        if (!this.canScrollRight()) {
            graphics.blit(GUI_TEXTURE, x+105, y+24, 182, 0, 6, 36);
        }
        if (!this.canTryCraft()) {
            graphics.blit(GUI_TEXTURE, x+50, y+61, 188, 0, 18, 18);
        }

        WorkbenchRecipe recipe = this.selectedRecipe < 0 ? null : this.availableRecipes.get(this.selectedRecipe);

        if (recipe != null && !recipe.result().isEmpty()) {
            poseStack.pushPose();
            graphics.drawString(this.font, recipe.result().getHoverName(), x+this.itemNameX, y+this.itemNameY, -1);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            int dx = 2*(x+this.ingredientListX);
            int dy = 2*(y+this.ingredientListY);
            graphics.drawString(this.font, Component.translatable("text.bmnw.ingredients").getString(),
                    dx, dy, -1);
            int indexator = (int) Math.abs(System.currentTimeMillis() / 1000);
            Map<Ingredient, Integer> nameIntMap = new HashMap<>();
            for (Ingredient ingredient : recipe.ingredients()) {
                nameIntMap.putIfAbsent(ingredient, 0);
                nameIntMap.computeIfPresent(ingredient, (k, v) -> v + 1);
            }
            int next = 10;
            for (Map.Entry<Ingredient, Integer> entry : nameIntMap.entrySet()) {
                ItemStack[] items = entry.getKey().getItems();
                if (items.length == 0) continue;
                ItemStack item = items[indexator % items.length];
                Component name = Component.literal(entry.getValue() + "x ").append(item.getHoverName());
                graphics.drawString(this.font, name, dx, dy+next, -1);
                next += 8;
            }
            poseStack.popPose();
        }

        int startIndex = this.selectionScroll * 2;
        int endIndex = this.maxRecipeIdx > 10+startIndex ? 10 : this.maxRecipeIdx - startIndex;
        for (int i = 0; i < endIndex; i++) {
            int idx = startIndex + i;
            WorkbenchRecipe rcp = this.availableRecipes.get(idx);
            ItemStack stack = Objects.requireNonNull(rcp, "Null recipe!").result();
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                int xd = 18*(idx / 2)+x+15;
                int yd = 18*(idx % 2)+y+25;
                graphics.renderItem(stack, xd, yd);
                graphics.renderItemDecorations(this.font, stack, xd, yd);
                poseStack.popPose();
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean success = super.mouseClicked(mouseX, mouseY, button);
        if (button == 0) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;
            return this.mouseClicked((int) mouseX - x, (int) mouseY - y) || success;
        }
        return success;
    }

    protected boolean mouseClicked(int mouseX, int mouseY) {
        if (this.canScrollLeft() && this.scrollLeft.isInArea(mouseX, mouseY)) {
            if (this.selectionScroll > 0) this.selectionScroll--;
            BMNWSounds.playClick();
            return true;
        }
        if (this.canScrollRight() && this.scrollRight.isInArea(mouseX, mouseY)) {
            this.selectionScroll++;
            BMNWSounds.playClick();
            return true;
        }
        if (this.canTryCraft() && this.craft.isInArea(mouseX, mouseY)) {
            WorkbenchRecipe recipe = this.selectedRecipe < 0 ? null : this.availableRecipes.get(this.selectedRecipe);
            if (recipe != null) {
                PacketDistributor.sendToServer(new PacketWorkbenchCraft(recipe.rsl().toString(), Screen.hasShiftDown()));
            }
            BMNWSounds.playClickOld();
            return true;
        }
        for (int i = 0; i < 10; i++) {
            int idx = this.selectionScroll * 2 + i;
            if (idx < this.maxRecipeIdx) {
                MouseArea area = this.craftingEntries[i];
                if (area.isInArea(mouseX, mouseY)) {
                    this.selectedRecipe = idx;
                    BMNWSounds.playClick();
                }
            }
        }
        return false;
    }

    protected boolean canScrollLeft() {
        return this.selectionScroll > 0;
    }
    protected boolean canScrollRight() {
        return this.selectionScroll < this.maxSelectionScroll;
    }
    protected boolean canTryCraft() {
        return this.selectedRecipe >= 0;
    }
}
