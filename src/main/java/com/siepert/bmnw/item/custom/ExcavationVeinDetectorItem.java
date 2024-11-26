package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.misc.ExcavationVein;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ExcavationVeinDetectorItem extends Item {
    public ExcavationVeinDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ChunkAccess chunk = level.getChunk(player.getOnPos());
        ExcavationVein vein = ExcavationVein.getNextVein(chunk);
        if (!level.isClientSide()) player.sendSystemMessage(Component.translatable("text.bmnw.excavation_vein_detector_msg",
                        vein.getName(), chunk.getPos().x, chunk.getPos().z));
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) return;
        if (entity instanceof Player player && isSelected) {
            ChunkAccess chunk = level.getChunk(entity.getOnPos());
            final ExcavationVein vein = ExcavationVein.getNextVein(chunk);
            player.displayClientMessage(Component.translatable("text.bmnw.excavation_vein_detector", vein.getName()), true);
        }
    }
}
