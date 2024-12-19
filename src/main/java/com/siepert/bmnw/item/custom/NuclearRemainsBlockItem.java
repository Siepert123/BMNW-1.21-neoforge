package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class NuclearRemainsBlockItem extends SimpleRadioactiveBlockItem {
    public <T extends Block> NuclearRemainsBlockItem(T block, Properties properties) {
        super(block, properties);
        HazardRegistry.addBurningRegistry(this, true);
    }
}
