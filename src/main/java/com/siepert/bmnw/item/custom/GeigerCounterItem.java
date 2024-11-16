package com.siepert.bmnw.item.custom;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModSounds;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
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
            long chunkFemtoRads = myChunk.getData(ModAttachments.RADIATION);
            long playerFemtoRads = player.getPersistentData().getLong(RadHelper.RAD_NBT_TAG);

            player.sendSystemMessage(Component.literal(String.format("Chunk radiation: %sRAD\nPlayer radiation: %sRAD",
                    UnitConvertor.display(chunkFemtoRads),
                    UnitConvertor.display(playerFemtoRads)
            )));

        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    private long invRads(Player player) {
        long invFemtoRads = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof IItemHazard) {
                invFemtoRads += (((IItemHazard) stack.getItem()).radioactivity() * stack.getCount());
            }
        }
        return invFemtoRads;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            ChunkAccess myChunk = level.getChunk(entity.getOnPos());

            if (RadHelper.geigerTick(RadHelper.getChunkRadiation(myChunk) + invRads(player), RANDOM)) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.GEIGER_CLICK, SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
        }
    }
}
