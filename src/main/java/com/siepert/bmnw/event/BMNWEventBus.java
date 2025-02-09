package com.siepert.bmnw.event;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.block.entity.custom.IronBarrelBlockEntity;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.block.entity.renderer.HatchRenderer;
import com.siepert.bmnw.datagen.BMNWAdvancementGenerator;
import com.siepert.bmnw.effect.BMNWEffects;
import com.siepert.bmnw.entity.BMNWEntityTypes;
import com.siepert.bmnw.entity.renderer.*;
import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.item.BMNWItems;
import com.siepert.bmnw.item.custom.CoreSampleItem;
import com.siepert.bmnw.misc.*;
import com.siepert.bmnw.particle.BMNWParticleTypes;
import com.siepert.bmnw.particle.custom.*;
import com.siepert.bmnw.radiation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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
import java.util.Map;
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
                final float radiationRemoveRate = 0.9f;
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

                            if (BMNWConfig.recalculateChunks) {
                                if (level.getGameTime() % BMNWConfig.chunkRecalculationInterval == 0) {
                                    RadiationManager.getInstance().recalculateChunkSources(chunk);
                                }
                            }
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
                    RadiationManager.getInstance().tick(event.getServer());
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
            if (BMNWConfig.radiationSetting.chunk() && !event.getEntity().level().isClientSide()) {
                if (event.getEntity() instanceof LivingEntity entity) {
                    if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;
                    CompoundTag nbt = entity.getPersistentData();
                }
            }
        }

        /**
         * Handles the negative side effects of radiation on entities.
         */
        @SubscribeEvent
        public static void entityTickEventPost(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof LivingEntity entity && !event.getEntity().level().isClientSide()) {
                if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;
                CompoundTag nbt = entity.getPersistentData();

                float rads = nbt.getFloat(RadiationManager.rad_nbt_tag);
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

        //endregion

        /**
         * Calculates source radioactivity of a chunk when it's generated.
         */
        @SubscribeEvent
        public static void chunkEventLoad(ChunkEvent.Load event) {
            if (BMNWConfig.radiationSetting.chunk()) {
                if (event.isNewChunk()) {
                    if (RadiationManager.getInstance() != null) {
                        RadiationManager.getInstance().recalculateChunkSources(event.getChunk());
                    } else RadiationManager.addToQueue(event.getChunk());
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
                if (player.isCreative() || player.level().isClientSide()) return;
                for (ItemStack stack : player.getInventory().items) {
                    Item item = stack.getItem();

                    if (HazardRegistry.getRadRegistry(item) > 0) {
                        RadiationManager.getInstance().addEntityRadiation(player, HazardRegistry.getRadRegistry(item) * stack.getCount() / 20);
                    }
                    if (HazardRegistry.getBurningRegistry(item)) {
                        player.setRemainingFireTicks(20);
                    }
                    if (HazardRegistry.getBlindingRegistry(item)) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));
                    }
                }
            }
        }
        //endregion

        //region Block events
        @SubscribeEvent
        public static void blockEventPlace(BlockEvent.EntityPlaceEvent event) {
            if (BMNWConfig.recalculateOnBlockEvent
                    || HazardRegistry.getRadRegistry(event.getPlacedBlock().getBlock()) > 0) {
                RadiationManager.getInstance().putSource(event.getEntity().level().dimension().location(), event.getPos(),
                        HazardRegistry.getRadRegistry(event.getPlacedBlock().getBlock()), true);
            }
        }
        @SubscribeEvent
        public static void blockEventBreak(BlockEvent.BreakEvent event) {
            if (BMNWConfig.recalculateOnBlockEvent
                    || HazardRegistry.getRadRegistry(event.getState().getBlock()) > 0) {
                RadiationManager.getInstance().removeSource(event.getPlayer().level().dimension().location(), event.getPos());
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
            if (BMNWConfig.itemHazardInfo.id() > 0) {
                Item item = stack.getItem();
                float itemRads = HazardRegistry.getRadRegistry(item);
                if (itemRads > 0) {
                    if (BMNWConfig.itemHazardInfo.id() == 2) {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive")
                                .append(" - ").append(String.valueOf(itemRads)).append("RAD/s").withColor(0x00ff00));
                    } else {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive").withColor(0x00ff00));
                    }
                }
                if (HazardRegistry.getBurningRegistry(item)) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.burning").withColor(0xffff00));
                }
                if (HazardRegistry.getBlindingRegistry(item)) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.blinding").withColor(0x7777ff));
                }
            }
            if (stack.getItem() instanceof CoreSampleItem) {
                if (stack.is(BMNWItems.EMPTY_CORE_SAMPLE)) event.addTooltipLines(
                        Component.translatable("tooltip.bmnw.coreSample.empty").withColor(0x888888)
                );
                else {
                    final ExcavationVein vein = ((CoreSampleItem) stack.getItem()).getVein();

                    final Map<Item, Integer> itemMap = vein.getItemToIntMap();

                    for (Map.Entry<Item, Integer> entry : itemMap.entrySet()) {
                        event.addTooltipLines(entry.getKey().getName(new ItemStack(entry.getKey())).copy()
                                .append(": " + vein.percent(entry.getValue()) + "%").withColor(0x888888));
                    }
                }
            }
        }
    }

    @EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {

        @OnlyIn(Dist.CLIENT)
        private static <T extends Entity, V extends T> void registerEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers event,
                                                                                           Supplier<EntityType<V>> type,
                                                                                           EntityRendererProvider<T> renderer) {
            event.registerEntityRenderer(type.get(), renderer);
        }
        private static <T extends BlockEntity, V extends T> void registerBlockEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers event,
                                                                                                     Supplier<BlockEntityType<V>> type,
                                                                                                     BlockEntityRendererProvider<T> renderer) {
            event.registerBlockEntityRenderer(type.get(), renderer);
        }

        @OnlyIn(Dist.CLIENT)
        private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerBlockEntityRenderingHandler(event, BMNWBlockEntities.HATCH, HatchRenderer::new);
        }

        @OnlyIn(Dist.CLIENT)
        private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerEntityRenderingHandler(event, BMNWEntityTypes.BLOCK_DEBRIS, BlockDebrisRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.NUCLEAR_CHARGE, NuclearChargeRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.DUD, DudRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.LITTLE_BOY, LittleBoyRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.CASEOH, CaseohRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.ANTI_MISSILE_MISSILE, AntiMissileMissileRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.EXAMPLE_MISSILE, ExampleMissileRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.HE_MISSILE, HighExplosiveMissileRenderer::new);
            registerEntityRenderingHandler(event, BMNWEntityTypes.NUCLEAR_MISSILE, NuclearMissileRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.LEAD_BULLET, LeadBulletRenderer::new);
        }

        /**
         * Registers entity renderers.
         */
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void entityRenderersEventRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            if (DistrictHolder.getDistrict().isClient()) {
                registerBlockEntityRenderers(event);
                registerEntityRenderers(event);
            }
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
            event.registerSpriteSet(BMNWParticleTypes.LARGE_MISSILE_SMOKE.get(), LargeMissileSmokeParticle.Provider::new);
            event.registerSpriteSet(BMNWParticleTypes.DUSTY_FIRE.get(), DustyFireParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerCapabilitiesEvent(RegisterCapabilitiesEvent event) {
            event.registerBlock(
                    Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, blockEntity, context) -> blockEntity != null ?
                            ((MissileLaunchPadBlockEntity) blockEntity).getIEnergy() : null,
                    BMNWBlocks.MISSILE_LAUNCH_PAD.get()
            );
            event.registerBlock(
                    Capabilities.FluidHandler.BLOCK,
                    (level, pos, state, blockEntity, context) -> blockEntity != null?
                            ((IronBarrelBlockEntity)blockEntity).getIFluid() : null,
                    BMNWBlocks.IRON_BARREL.get()
            );
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
