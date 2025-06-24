package nl.melonstudios.bmnw.item.colorize;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidContainerColorizer implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {

        }
        return 0xFFFFFFFF;
    }
}
