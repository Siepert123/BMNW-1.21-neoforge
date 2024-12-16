package com.siepert.bmnw.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;

import java.util.List;

public class CommunistManifestoItem extends WrittenBookItem {
    public CommunistManifestoItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.bmnw.communist_manifesto");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("book.byAuthor", "Karl Marx").withColor(0xaaaaaa));
    }
}
