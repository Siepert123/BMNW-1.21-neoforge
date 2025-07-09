package nl.melonstudios.bmnw.item.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import nl.melonstudios.bmnw.init.BMNWTags;
import nl.melonstudios.bmnw.interfaces.IExtremelyHotOverride;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FluidContainerItem extends Item implements IExtremelyHotOverride {
    public static String formatNicely(int mB) {
        if (mB > 10000000) {
            return Mth.quantize((mB / 1000000.0F) * 100.0F, 1) / 100.0F + "kB";
        }
        if (mB > 10000) {
            return Mth.quantize((mB / 1000.0F) * 100.0F, 1) / 100.0F + "B";
        }
        return mB + "mB";
    }

    private static final HashSet<FluidContainerItem> ALL_FLUID_CONTAINERS = new HashSet<>();
    public static Set<FluidContainerItem> getAllFluidContainers() {
        return Set.of(ALL_FLUID_CONTAINERS.toArray(new FluidContainerItem[0]));
    }
    public FluidContainerItem(Properties properties) {
        super(properties);
        ALL_FLUID_CONTAINERS.add(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null) {
            FluidStack fluidStack = handler.getFluidInTank(0);
            if (!fluidStack.isEmpty()) {
                int fill = fluidStack.getAmount();
                int max = handler.getTankCapacity(0);
                Component component = Component.literal(String.format("%s/%s ", formatNicely(fill), formatNicely(max))).withColor(0x888888);
                tooltipComponents.add(component);
            }
        } else {
            tooltipComponents.add(Component.literal("<No FluidHandler>").withColor(0xFF0000));
        }
    }

    @Nullable
    public static IFluidHandlerItem getHandler(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM);
    }
    public static FluidStack getContents(ItemStack stack) {
        IFluidHandlerItem handler = getHandler(stack);
        return handler == null ? FluidStack.EMPTY : handler.getFluidInTank(0);
    }
    public static boolean isStackable(ItemStack stack) {
        IFluidHandlerItem handler = getHandler(stack);
        if (handler == null) return true;
        FluidStack fluidStack = handler.getFluidInTank(0);
        if (fluidStack.isEmpty()) return true;
        return fluidStack.getAmount() == handler.getTankCapacity(0);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IFluidHandlerItem handler = getHandler(stack);
        if (handler == null) return 0;
        int fill = handler.getFluidInTank(0).getAmount();
        int max = handler.getTankCapacity(0);
        return Math.round((fill * 13.0F) / max);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        FluidStack fluidStack = getContents(stack);
        if (fluidStack.isEmpty()) return false;
        IFluidHandlerItem handler = getHandler(stack);
        return handler != null && handler.getTankCapacity(0) != fluidStack.getAmount();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(1.0F / 3, 1, 1);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        String desc = this.getOrCreateDescriptionId();
        if (getContents(stack).isEmpty()) return desc;
        return desc + "_filled";
    }

    @Override
    public Component getName(ItemStack stack) {
        FluidStack fluidStack = getContents(stack);
        if (fluidStack.isEmpty()) return Component.translatable(this.getDescriptionId());
        return Component.translatable(this.getDescriptionId(stack), fluidStack.getFluidType().getDescription(fluidStack).getString());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isStackable(stack) ? super.getMaxStackSize(stack) : 1;
    }

    @Override
    public boolean isExtremelyHot(ItemStack stack) {
        return getContents(stack).is(BMNWTags.Fluids.HOT);
    }
}
