package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IFluidContainerItem {
    @OnlyIn(Dist.CLIENT)
    int getFluidColor(ItemStack stack);
}
