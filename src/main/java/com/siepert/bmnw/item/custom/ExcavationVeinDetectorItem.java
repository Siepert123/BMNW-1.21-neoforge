package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.misc.ExcavationVein;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ExcavationVeinDetectorItem extends Item {
    public ExcavationVeinDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ChunkPos pos = level.getChunk(player.getOnPos()).getPos();
        ExcavationVein vein = ExcavationVein.getNextVein(pos);
        if (!level.isClientSide()) player.displayClientMessage(Component.translatable("text.bmnw.excavation_vein_detector", vein.getName()),
                true);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
