package nl.melonstudios.bmnw.item.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import nl.melonstudios.bmnw.cfg.BMNWCommonConfig;
import nl.melonstudios.bmnw.init.BMNWAttachments;
import nl.melonstudios.bmnw.misc.ExcavationVein;

public class ExcavationVeinDetectorItem extends Item {
    public ExcavationVeinDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ChunkAccess chunk = level.getChunk(player.getOnPos());
        ExcavationVein vein = ExcavationVein.getNextVein(chunk);
        if (!level.isClientSide()) {
            if (BMNWCommonConfig.enableExcavationVeinDepletion) {
                if (vein.isEmpty())
                    player.sendSystemMessage(Component.translatable("text.bmnw.excavation_vein_detector_fail"));
                else {
                    int depletion = chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION);
                    int remaining = vein.getMaximumExtraction() - depletion;
                    player.sendSystemMessage(Component.translatable("text.bmnw.excavation_vein_detector_msg",
                            remaining, vein.getName()));
                }
            } else {
                player.getInventory().add(new ItemStack(vein.getCoreSample()));
            }
        }
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
