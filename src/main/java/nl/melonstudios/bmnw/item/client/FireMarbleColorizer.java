package nl.melonstudios.bmnw.item.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireMarbleColorizer implements ItemColor {
    private final RandomSource rnd = RandomSource.create();
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            Float chargeLevel = stack.get(BMNWDataComponents.FIRE_MARBLE_CHARGE);
            if (chargeLevel == null) return 0x00ffffff;
            int alpha = (int)(0xff * chargeLevel * (0.9f + rnd.nextFloat() * 0.1f));
            return 0x00ffffff | (alpha << 24);
        }
        if (tintIndex == 1) {
            Integer marbleType = stack.get(BMNWDataComponents.FIRE_MARBLE_TYPE);
            if (marbleType == null) return 0xffffffff;
            return Color.HSBtoRGB(marbleType / 6.0f, 1, 1);
        }
        return 0xffffffff;
    }
}
