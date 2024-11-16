package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Simple radioactive IItemHazard implementation.
 */
public class SimpleRadioactiveItem extends Item implements IItemHazard {
    private final long femtoRads;
    private final Component tooltip;
    public SimpleRadioactiveItem(Properties properties, long femtoRads) {
        super(properties);
        this.femtoRads = femtoRads;
        tooltip = Component.translatable("tooltip.bmnw.radioactive").append(" - ").append(UnitConvertor.display(femtoRads)).append("RAD/t");
    }

    @Override
    public long radioactivity() {
        return femtoRads;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(tooltip);
    }
}
