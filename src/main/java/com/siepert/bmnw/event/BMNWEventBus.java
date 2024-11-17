package com.siepert.bmnw.event;

import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.entity.renderer.EmptyEntityRenderer;
import com.siepert.bmnw.entity.renderer.NuclearChargeRenderer;
import com.siepert.bmnw.interfaces.IItemHazard;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModDamageSources;
import com.siepert.bmnw.particle.ModParticleTypes;
import com.siepert.bmnw.particle.custom.FireSmokeParticleProvider;
import com.siepert.bmnw.particle.custom.VomitParticleProvider;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.ShieldingValues;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.function.Supplier;

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

                    for (ChunkHolder chunkHolder : chunkHolders) {
                        LevelChunk chunk = chunkHolder.getTickingChunk();
                        if (chunk == null) continue;
                        if (chunk.getData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK)) chunk.setData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, false);

                        long femtoRads = chunk.getData(ModAttachments.RADIATION);
                        chunk.setData(ModAttachments.RADIATION, (long)(femtoRads * radiationRemoveRate));

                        chunk.setData(ModAttachments.RADIATION, chunk.getData(ModAttachments.RADIATION) + chunk.getData(ModAttachments.SOURCE_RADIOACTIVITY));

                        if (RadHelper.getChunkRadiation(chunk) > UnitConvertor.fromMilli(1)) RadHelper.disperseChunkRadiation(chunk);
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

                        if (UnitConvertor.toNormal(femtoRads) > 15000 || femtoRads < 0) chunk.setData(ModAttachments.RADIATION, UnitConvertor.fromKilo(15));

                        if (UnitConvertor.toNormal(femtoRads) > 5 && level.random.nextInt(50) == 0) RadHelper.createChunkRadiationEffects(chunk);
                    }
                }
            } catch (Exception ignored) {

            }
        }

        @SubscribeEvent
        public static void entityTickEventPre(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity instanceof Player player && player.isCreative()) return;
                CompoundTag nbt = entity.getPersistentData();

                ChunkAccess chunk = entity.level().getChunk(entity.getOnPos());

                RadHelper.addEntityRadiation(entity, RadHelper.getAdjustedRadiation(entity.level(), entity.getOnPos()));

                long entityFemtoRads = nbt.getLong(RadHelper.RAD_NBT_TAG);

                nbt.putLong(RadHelper.RAD_NBT_TAG, (long) (entityFemtoRads * 0.9999));
            }
        }

        @SubscribeEvent
        public static void entityTickEventPost(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity instanceof Player player && player.isCreative()) return;
                CompoundTag nbt = entity.getPersistentData();

                long entityFemtoRads = nbt.getLong(RadHelper.RAD_NBT_TAG);

                long normalized = UnitConvertor.toNormal(entityFemtoRads);

                if (normalized > 1000) {
                    if (entity.level().getGameTime() % 20 == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
                        entity.hurt(ModDamageSources.radiation(entity.level()), 8);
                    }
                }
                if (normalized > 800) {
                    if (entity.getRandom().nextInt(250) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
                    }
                }
                if (normalized > 600) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                    }
                    if (entity.getRandom().nextInt(500) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    }
                    if (entity.getRandom().nextInt(500) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 4));
                        Vec3 look = entity.getLookAngle();
                        RandomSource rand = entity.getRandom();
                        for (int i = 0; i < 50; i++) {
                            Vec3 vec = look.xRot(rand.nextFloat()).yRot(rand.nextFloat()).zRot(rand.nextFloat());
                            entity.level().addParticle(ModParticleTypes.VOMIT.get(),
                                    entity.getX(), entity.getEyeY(), entity.getZ(),
                                    vec.x(), vec.y(), vec.z());
                        }
                    }
                }
                if (normalized > 200) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2));
                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 1));
                    }
                }
                if (normalized > 100) {
                    if (entity.getRandom().nextInt(1000) == 0) {
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void playerTickEventPre(PlayerTickEvent.Pre event) {
            Player player = event.getEntity();
            if (player.isCreative()) return;
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
            registerEntityRenderingHandler(event, ModEntityTypes.NUCLEAR_CHARGE, NuclearChargeRenderer::new);
        }

        @SubscribeEvent
        public static void entityRenderersEventRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerBlockEntityRenderers(event);
            registerEntityRenderers(event);
        }

        @SubscribeEvent
        public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticleTypes.VOMIT.get(), VomitParticleProvider::new);
            event.registerSpriteSet(ModParticleTypes.FIRE_SMOKE.get(), FireSmokeParticleProvider::new);
        }
    }
}
