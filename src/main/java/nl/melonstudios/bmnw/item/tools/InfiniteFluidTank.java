package nl.melonstudios.bmnw.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import nl.melonstudios.bmnw.interfaces.IInfiniteFluidSupply;

public class InfiniteFluidTank extends Item implements IInfiniteFluidSupply {
    private final int perTick;
    public InfiniteFluidTank(Properties properties, int perTick) {
        super(properties.stacksTo(1));
        this.perTick = perTick;
    }

    @Override
    public boolean compatible(ItemStack stack, Fluid fluid) {
        return true;
    }

    @Override
    public int transferSpeed(ItemStack stack) {
        return this.perTick;
    }
}
