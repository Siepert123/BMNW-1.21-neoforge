package com.siepert.bmnw.event;

import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unchecked")
public class BMNWEventBus {
    private static final Logger LOGGER = LogManager.getLogger();

    @EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.GAME)
    public static class GameEventBus {
        @SubscribeEvent
        public static void serverTickEventPre(ServerTickEvent.Pre event) {
            final double radiationRemoveRate = 0.9999;
            try {
                for (ServerLevel level : event.getServer().getAllLevels()) {
                    if (level == null) continue;
                    Iterable<ChunkHolder> chunkHolders = (Iterable<ChunkHolder>)
                            ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks")
                                    .invoke(level.getChunkSource().chunkMap);
                    if (chunkHolders == null) continue;
                    if (false && level.getGameTime() % 10 == 0) {
                        for (ChunkHolder chunkHolder : chunkHolders) {
                            if (chunkHolder.getTickingChunk() == null) continue;
                            RadHelper.recalculateChunkRadioactivity(chunkHolder.getTickingChunk());
                        }
                    }

                    for (ChunkHolder chunkHolder : chunkHolders) {
                        LevelChunk chunk = chunkHolder.getTickingChunk();
                        if (chunk == null) continue;
                        if (chunk.getData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK)) chunk.setData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, false);

                        long femtoRads = chunk.getData(ModAttachments.RADIATION);
                        chunk.setData(ModAttachments.RADIATION, (long)(femtoRads * radiationRemoveRate));

                        chunk.setData(ModAttachments.RADIATION, chunk.getData(ModAttachments.RADIATION) + chunk.getData(ModAttachments.SOURCE_RADIOACTIVITY));
                    }
                }
            } catch (Exception ignored) {

            }
        }

        @SubscribeEvent
        public static void serverTickEventPost(ServerTickEvent.Post event) {
            try {
                for (ServerLevel level : event.getServer().getAllLevels()) {
                    if (level == null) continue;
                    Iterable<ChunkHolder> chunkHolders = (Iterable<ChunkHolder>)
                            ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks")
                                    .invoke(level.getChunkSource().chunkMap);
                    if (chunkHolders == null) continue;

                    for (ChunkHolder chunkHolder : chunkHolders) {
                        LevelChunk chunk = chunkHolder.getTickingChunk();
                        if (chunk == null) continue;

                        chunk.setData(ModAttachments.RADIATION, chunk.getData(ModAttachments.RADIATION) + chunk.getData(ModAttachments.QUEUED_RADIATION));
                        chunk.setData(ModAttachments.QUEUED_RADIATION, 0L);

                        long femtoRads = RadHelper.getChunkRadiation(chunk);

                        if (UnitConvertor.toNormal(femtoRads) > 5 && level.random.nextInt(50) == 0) RadHelper.createChunkRadiationEffects(chunk);
                    }
                }
            } catch (Exception ignored) {

            }
        }

        @SubscribeEvent
        public static void entityTickEventPre(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {

            }
        }

        @SubscribeEvent
        public static void entityTickEventPost(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof LivingEntity entity) {

            }
        }

        @SubscribeEvent
        public static void playerTickEventPre(PlayerTickEvent.Pre event) {
            Player player = event.getEntity();
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof IItemHazard itemHazard) {
                    if (itemHazard.radioactivity() > 0L) {
                        RadHelper.addEntityRadiation(player, itemHazard.radioactivity() * stack.getCount());
                    }

                    if (itemHazard.burning()) {
                        player.setRemainingFireTicks(20);
                    }

                    if (itemHazard.blinding()) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));
                    }
                }
            }
        }
    }

    //@EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {

    }
}
