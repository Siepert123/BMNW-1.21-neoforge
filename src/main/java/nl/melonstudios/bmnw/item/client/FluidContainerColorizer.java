package nl.melonstudios.bmnw.item.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import nl.melonstudios.bmnw.misc.Library;

@OnlyIn(Dist.CLIENT)
public class FluidContainerColorizer implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null) {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if (fluidStack.isEmpty()) return 0;
                IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluidType());
                Material material = ClientHooks.getBlockMaterial(extensions.getStillTexture(fluidStack));
                TextureAtlasSprite sprite = material.sprite();
                return FastColor.ARGB32.multiply(Library.convertABGRtoARGB(Library.getCenterColorRGBA(sprite.contents().getOriginalImage())),
                        extensions.getTintColor(fluidStack));
            }
        }
        return -1;
    }
}
