package nl.melonstudios.bmnw.item.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.misc.FluidTextureData;
import nl.melonstudios.bmnw.misc.Library;

import java.util.HashMap;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class FluidIdentifierColorizer implements ItemColor {
    private final HashMap<String, Fluid> quickLookup = new HashMap<>();

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            try {
                if (!stack.has(BMNWDataComponents.FLUID_TYPE)) return 0;
                Fluid fluid = this.quickLookup.computeIfAbsent(stack.get(BMNWDataComponents.FLUID_TYPE),
                        (k) -> BuiltInRegistries.FLUID.get(ResourceLocation.parse(Objects.requireNonNull(k))));
                FluidTextureData data = FluidTextureData.getStillTexture(fluid);
                TextureAtlasSprite sprite = data.material().sprite();
                return FastColor.ARGB32.multiply(Library.convertABGRtoARGB(Library.getCenterColorRGBA(sprite.contents().getOriginalImage())),
                        data.extensions().getTintColor());
            } catch (Throwable e) {
                return 0;
            }
        }
        return -1;
    }
}
