package nl.melonstudios.bmnw.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import nl.melonstudios.bmnw.interfaces.IInfiniteFluidSupply;

public class InfiniteWaterTank extends Item implements IInfiniteFluidSupply {
    private final int perTick;
    public InfiniteWaterTank(Properties properties, int perTick) {
        super(properties.stacksTo(1));
        this.perTick = perTick;
    }

    @Override
    public boolean compatible(ItemStack stack, Fluid fluid) {
        return fluid.isSame(Fluids.WATER);
    }

    @Override
    public int transferSpeed(ItemStack stack) {
        return this.perTick;
    }
}
