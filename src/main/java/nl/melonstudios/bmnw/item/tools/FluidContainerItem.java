package nl.melonstudios.bmnw.item.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import nl.melonstudios.bmnw.interfaces.IFluidContainerItem;
import nl.melonstudios.bmnw.item.battery.BatteryItem;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class FluidContainerItem extends Item implements IFluidContainerItem {
    public static String formatNicely(int mB) {
        if (mB > 10000000) {
            return Mth.quantize((mB / 1000000.0F) * 100.0F, 1) / 100.0F + "kB";
        }
        if (mB > 10000) {
            return Mth.quantize((mB / 1000.0F) * 100.0F, 1) / 100.0F + "B";
        }
        return mB + "mB";
    }

    public FluidContainerItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getFluidColor(ItemStack stack) {
        IFluidHandlerItem fluid = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluid != null) {
            FluidStack fluidStack = fluid.getFluidInTank(0);
            if (fluidStack.isEmpty()) return 0;
            if (fluidStack.is(Fluids.LAVA)) return 0xFFFF0000;
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluidType());
            return extensions.getTintColor(fluid.getFluidInTank(0));
        }
        return Color.HSBtoRGB((float)GLFW.glfwGetTime() / 10.0F, 1.0F, 1.0F);
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
    private static IFluidHandlerItem getHandler(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM);
    }
    private static FluidStack getContents(ItemStack stack) {
        IFluidHandlerItem handler = getHandler(stack);
        return handler == null ? FluidStack.EMPTY : handler.getFluidInTank(0);
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
}
