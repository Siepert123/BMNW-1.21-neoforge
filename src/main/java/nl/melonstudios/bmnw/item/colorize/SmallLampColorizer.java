package nl.melonstudios.bmnw.item.colorize;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.item.misc.SmallLampBlockItem;
import nl.melonstudios.bmnw.misc.SI;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallLampColorizer implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (stack.getItem() instanceof SmallLampBlockItem item && tintIndex > 0 && tintIndex < 4) {
            DyeColor color = item.getBlock().color;
            boolean active = item.getBlock().inverted;
            float red = FastColor.ARGB32.red(color.getTextureDiffuseColor()) / 255.0F;
            float green = FastColor.ARGB32.green(color.getTextureDiffuseColor()) / 255.0F;
            float blue = FastColor.ARGB32.blue(color.getTextureDiffuseColor()) / 255.0F;
            if (active) {
                return FastColor.ARGB32.colorFromFloat(1.0F, red, green, blue);
            }
            red *= 0.75F;
            green *= 0.75F;
            blue *= 0.75F;
            float colorMul = SI.BRIGHTNESS_BY_AXIS[tintIndex-1];
            red *= colorMul;
            green *= colorMul;
            blue *= colorMul;
            return FastColor.ARGB32.colorFromFloat(1.0F, red, green, blue);
        }
        return 0xFFFFFFFF;
    }
}
