package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.misc.BMNWAttachments;
import com.siepert.bmnw.misc.BMNWSounds;
import com.siepert.bmnw.radiation.RadHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

public class GeigerCounterItem extends Item {
    private static final Random RANDOM = new Random();
    public GeigerCounterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            ChunkAccess myChunk = level.getChunk(player.getOnPos());
            float chunkRads = myChunk.getData(BMNWAttachments.RADIATION);
            float playerRads = player.getPersistentData().getFloat(RadHelper.RAD_NBT_TAG);

            player.sendSystemMessage(Component.literal(String.format("Chunk radiation: %sRAD\nPlayer radiation: %sRAD",
                    BMNWConfig.radiationSetting.chunk() ? chunkRads : 0,
                    BMNWConfig.radiationSetting.item() ? playerRads : 0
            )));

        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    private float invRads(Player player) {
        if (!BMNWConfig.radiationSetting.item()) return 0.0f;

        float inventoryRads = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof IItemHazard) {
                inventoryRads += (((IItemHazard) stack.getItem()).getRadioactivity() * stack.getCount());
            }
        }
        return inventoryRads;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            ChunkAccess myChunk = level.getChunk(entity.getOnPos());

            if (RadHelper.geigerTick((BMNWConfig.radiationSetting.chunk() ? RadHelper.getAdjustedRadiation(player.level(), player.getOnPos()) : 0)
                    + invRads(player), RANDOM)) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        BMNWSounds.GEIGER_CLICK, SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
        }
    }
}
