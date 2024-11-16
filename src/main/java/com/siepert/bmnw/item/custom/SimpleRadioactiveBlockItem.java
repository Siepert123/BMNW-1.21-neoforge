package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SimpleRadioactiveBlockItem extends BlockItem implements IItemHazard {
    private final long femtoRads;
    private final Component tooltip;
    public <T extends Block & IRadioactiveBlock> SimpleRadioactiveBlockItem(T block, Properties properties) {
        super(block, properties);
        this.femtoRads = block.radioactivity();
        tooltip = Component.translatable("tooltip.bmnw.radioactive").append(" - ").append(UnitConvertor.display(femtoRads)).append("RAD/t").withColor(0x00dd00);
    }
    public SimpleRadioactiveBlockItem(Block block, Properties properties, long femtoRads) {
        super(block, properties);
        this.femtoRads = femtoRads;
        tooltip = Component.translatable("tooltip.bmnw.radioactive").append(" - ").append(UnitConvertor.display(femtoRads)).append("RAD/t").withColor(0x00dd00);
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
