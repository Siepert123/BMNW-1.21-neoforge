package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.block.custom.SimpleRadioactiveBlock;
import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Simple radioactive item implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveBlock
 */
public class SimpleRadioactiveItem extends Item implements IItemHazard {
    private final float rads;
    private final Component tooltip;
    public SimpleRadioactiveItem(Properties properties, float rads) {
        super(properties);
        this.rads = rads;
        tooltip = Component.translatable("tooltip.bmnw.radioactive").append(" - ").append(String.valueOf(rads)).append("RAD/s").withColor(0x00dd00);
    }

    @Override
    public float radioactivity() {
        return rads;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(tooltip);
    }
}
