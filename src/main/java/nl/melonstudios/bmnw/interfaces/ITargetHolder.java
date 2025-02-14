package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ITargetHolder {
    @Nullable
    BlockPos getTarget(ItemStack stack);
}
