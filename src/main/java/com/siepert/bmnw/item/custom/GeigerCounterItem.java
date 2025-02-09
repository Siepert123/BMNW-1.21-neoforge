package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.radiation.RadiationManager;
import net.minecraft.network.chat.Component;
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
            float chunkRads = 0;
            float playerRads = player.getPersistentData().getFloat(RadiationManager.rad_nbt_tag);

            player.sendSystemMessage(Component.literal(String.format("World radiation: %sRAD\nPlayer radiation: %sRAD",
                    "WIP!",
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

        }
    }
}
