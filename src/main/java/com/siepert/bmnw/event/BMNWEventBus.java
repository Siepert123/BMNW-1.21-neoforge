package com.siepert.bmnw.event;

import com.siepert.bmnw.datagen.BMNWAdvancementGenerator;
import com.siepert.bmnw.datagen.BMNWItemTagGenerator;
import com.siepert.bmnw.effect.BMNWEffects;
import com.siepert.bmnw.entity.BMNWEntityTypes;
import com.siepert.bmnw.entity.renderer.*;
import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.item.BMNWItems;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.misc.BMNWAttachments;
import com.siepert.bmnw.misc.BMNWDamageSources;
import com.siepert.bmnw.particle.BMNWParticleTypes;
import com.siepert.bmnw.particle.custom.*;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.ShieldingValues;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * BMNWs event bus.
 */
@SuppressWarnings("unchecked")
public class BMNWEventBus {
    private static final Logger LOGGER = LogManager.getLogger();

    @EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.GAME)
    public static class GameEventBus {
        //region Server tick events
        /**
         * Irradiates chunks based on source radioactivity (calculated seperately).
         * Could break at any moment, really.
         */
        @SubscribeEvent
        public static void serverTickEventPre(ServerTickEvent.Pre event) {
            if (BMNWConfig.radiationSetting.chunk()) {
                final float radiationRemoveRate = 0.9999f;
                try {
                    for (ServerLevel level : event.getServer().getAllLevels()) {
                        if (level == null) continue;
                        Iterable<ChunkHolder> chunkHolders = (Iterable<ChunkHolder>)
                                ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks")
                                        .invoke(level.getChunkSource().chunkMap);
                        if (chunkHolders == null) continue;

                        for (ChunkHolder chunkHolder : chunkHolders) {
                            LevelChunk chunk = chunkHolder.getTickingChunk();
                            if (chunk == null || !level.isLoaded(chunk.getPos().getMiddleBlockPosition(64))) continue;
                            if (chunk.getData(BMNWAttachments.SOURCED_RADIOACTIVITY_THIS_TICK))
                                chunk.setData(BMNWAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, false);

                            float rads = chunk.getData(BMNWAttachments.RADIATION);
                            chunk.setData(BMNWAttachments.RADIATION, (rads * radiationRemoveRate));

                            if (BMNWConfig.recalculateChunks) {
                                if (level.getGameTime() % BMNWConfig.chunkRecalculationInterval == 0) {
                                    RadHelper.recalculateChunkRadioactivity(chunk);
                                }
                            }

                            chunk.setData(BMNWAttachments.RADIATION, chunk.getData(BMNWAttachments.RADIATION) + (chunk.getData(BMNWAttachments.SOURCE_RADIOACTIVITY) / 200));

                            if (RadHelper.getChunkRadiation(chunk) > 1)
                                RadHelper.disperseChunkRadiation(chunk);
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }

        /**
         * Spreads radiation in chunks.
         * Could break at any moment, really.
         */
        @SubscribeEvent
        public static void serverTickEventPost(ServerTickEvent.Post event) {
            if (BMNWConfig.radiationSetting.chunk()) {
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

                            chunk.setData(BMNWAttachments.RADIATION, chunk.getData(BMNWAttachments.RADIATION) + chunk.getData(BMNWAttachments.QUEUED_RADIATION));
                            chunk.setData(BMNWAttachments.QUEUED_RADIATION, 0.0f);

                            float rads = RadHelper.getChunkRadiation(chunk);

                            if (rads > Float.MAX_VALUE || rads < 0 || Float.isNaN(rads))
                                chunk.setData(BMNWAttachments.RADIATION, 15000.0f);

                            if (rads > 100 && level.random.nextInt(50) == 0)
                                RadHelper.createChunkRadiationEffects(chunk);
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
        //endregion

        //region Entity tick events
        /**
         * Handles contamination and decontamination of entities.
         */
        @SubscribeEvent
        public static void entityTickEventPre(EntityTickEvent.Pre event) {
            if (BMNWConfig.radiationSetting.chunk()) {
                if (event.getEntity() instanceof LivingEntity entity) {
                    if (entity instanceof Player player && player.isCreative()) return;
                    CompoundTag nbt = entity.getPersistentData();

                    RadHelper.addEntityRadiation(entity, (RadHelper.getAdjustedRadiation(entity.level(), entity.getOnPos()) / 20f));

                    float rads = nbt.getFloat(RadHelper.RAD_NBT_TAG);

                    if (rads > 15000 || rads < 0)
                        nbt.putFloat(RadHelper.RAD_NBT_TAG, 15000.0f);
                    else nbt.putFloat(RadHelper.RAD_NBT_TAG, rads * 0.9999f);
                }
            }
        }

        /**
         * Handles the negative side effects of radiation on entities.
         */
        @SubscribeEvent
        public static void entityTickEventPost(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity instanceof Player player && player.isCreative()) return;
                CompoundTag nbt = entity.getPersistentData();

                float rads = nbt.getFloat(RadHelper.RAD_NBT_TAG);
                if (rads > 15000 || rads < 0) rads = 15000.0f;

                if (rads > 1000) {
                    if (entity.level().getGameTime() % 20 == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
                        entity.hurt(BMNWDamageSources.radiation(entity.level()), 8);
                    }
                }
                if (rads > 800) {
                    if (entity.getRandom().nextInt(250) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
                    }
                }
                if (rads > 600) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                    }
                    if (entity.getRandom().nextInt(500) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    }
                    if (entity.getRandom().nextInt(500) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 4));
                        entity.addEffect(new MobEffectInstance(BMNWEffects.VOMITING, 20));
                    }
                }
                if (rads > 200) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2));
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                    }
                }
                if (rads > 100) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100));
                    }
                }
            }
        }

        /**
         * Calculates source radioactivity of a chunk when it's generated.
         */
        @SubscribeEvent
        public static void chunkEventLoad(ChunkEvent.Load event) {
            if (BMNWConfig.radiationSetting.chunk()) {
                if (event.isNewChunk()) {
                    RadHelper.recalculateChunkRadioactivity(event.getChunk());
                }
            }
        }

        /**
         * Handles evil item effects.
         */
        @SubscribeEvent
        public static void playerTickEventPre(PlayerTickEvent.Pre event) {
            if (BMNWConfig.radiationSetting.item()) {
                Player player = event.getEntity();
                if (player.isCreative()) return;
                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() instanceof IItemHazard itemHazard) {
                        if (itemHazard.getRadioactivity() > 0.0f) {
                            RadHelper.addEntityRadiation(player, (itemHazard.getRadioactivity() * stack.getCount()) / 20);
                        }

                        if (itemHazard.isBurning()) {
                            player.setRemainingFireTicks(20);
                        }

                        if (itemHazard.isBlinding()) {
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));
                        }
                    }
                }
            }
        }
        //endregion

        //region Block events
        @SubscribeEvent
        public static void blockEventPlace(BlockEvent.EntityPlaceEvent event) {
            if (ShieldingValues.shields(event.getPlacedBlock()) || BMNWConfig.recalculateOnBlockEvent) {
                RadHelper.recalculateChunkRadioactivity(event.getLevel().getChunk(event.getPos()));
            }
        }
        @SubscribeEvent
        public static void blockEventBreak(BlockEvent.BreakEvent event) {
            if (ShieldingValues.shields(event.getState()) || BMNWConfig.recalculateOnBlockEvent) {
                RadHelper.recalculateChunkRadioactivity(event.getLevel().getChunk(event.getPos()));
            }
        }
        //endregion

        @SubscribeEvent
        public static void addAttributeTooltipsEvent(AddAttributeTooltipsEvent event) {
            ItemStack stack = event.getStack();
            if (stack.is(BMNWItems.BASE_MISSILE.get())) {
                event.addTooltipLines(Component.translatable("tooltip.bmnw.crafting_part").withColor(0x888888));
                return;
            }
            if (stack.getItem() instanceof BlockItem && BMNWConfig.itemHazardInfo.id() > 0) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (ShieldingValues.shields(block.defaultBlockState())) {
                    if (BMNWConfig.itemHazardInfo.id() == 2) {
                        event.addTooltipLines(
                                Component.translatable("tooltip.bmnw.radiation_shielding")
                                        .append(" - ")
                                        .append(String.valueOf(Math.round(100 * (1.0f - ShieldingValues.getShieldingModifier(block.defaultBlockState())))))
                                        .append("%")
                                        .withColor(0x00ff00)
                        );
                    } else {
                        event.addTooltipLines(
                                Component.translatable("tooltip.bmnw.radiation_shielding")
                                        .withColor(0x00ff00)
                        );
                    }
                }
            }
            if (stack.getItem() instanceof IItemHazard hazard && BMNWConfig.itemHazardInfo.id() > 0) {
                if (hazard.getRadioactivity() > 0) {
                    if (BMNWConfig.itemHazardInfo.id() == 2) {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive")
                                .append(" - ").append(String.valueOf(hazard.getRadioactivity())).append("RAD/s").withColor(0x00ff00));
                    } else {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive").withColor(0x00ff00));
                    }
                }
                if (hazard.isBurning()) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.burning").withColor(0xffff00));
                }
                if (hazard.isBlinding()) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.blinding").withColor(0x0000ff));
                }
            }
        }
    }

    @EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {
        private static <T extends Entity, V extends T> void registerEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers event,
                                                                                           Supplier<EntityType<V>> type,
                                                                                           EntityRendererProvider<T> renderer) {
            event.registerEntityRenderer(type.get(), renderer);
        }
        private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        }
        private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerEntityRenderingHandler(event, BMNWEntityTypes.BLOCK_DEBRIS, BlockDebrisRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.NUCLEAR_CHARGE, NuclearChargeRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.DUD, DudRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.LITTLE_BOY, LittleBoyRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.CASEOH, CaseohRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.EXAMPLE_MISSILE, ExampleMissileRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.HE_MISSILE, HighExplosiveMissileRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.NUCLEAR_MISSILE, NuclearMissileRenderer::new);
        }

        /**
         * Registers entity renderers.
         */
        @SubscribeEvent
        public static void entityRenderersEventRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerBlockEntityRenderers(event);
            registerEntityRenderers(event);
        }

        /**
         * Registers particle providers.
         */
        @SubscribeEvent
        public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(BMNWParticleTypes.VOMIT.get(), VomitParticleProvider::new);
            event.registerSpriteSet(BMNWParticleTypes.EVIL_FOG.get(), EvilFogParticleProvider::new);
            event.registerSpriteSet(BMNWParticleTypes.FIRE_SMOKE.get(), FireSmokeParticleProvider::new);
            event.registerSpriteSet(BMNWParticleTypes.SMOKE_HD.get(), SmokeHDParticleProvider::new);
            event.registerSpriteSet(BMNWParticleTypes.SHOCKWAVE.get(), ShockwaveParticleProvider::new);
        }

        /**
         * datagen (ew)
         */
        @SubscribeEvent
        public static void gatherDataEvent(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();
            CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

            generator.addProvider(
                    event.includeServer(),
                    new AdvancementProvider(
                            output, lookupProvider, existingFileHelper,
                            List.of(new BMNWAdvancementGenerator())
                    )
            );
        }
    }
}
