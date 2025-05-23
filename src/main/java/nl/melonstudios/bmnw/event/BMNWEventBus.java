package nl.melonstudios.bmnw.event;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.block.entity.IronBarrelBlockEntity;
import nl.melonstudios.bmnw.block.entity.MissileLaunchPadBlockEntity;
import nl.melonstudios.bmnw.block.entity.renderer.HatchRenderer;
import nl.melonstudios.bmnw.block.entity.renderer.PressRenderer;
import nl.melonstudios.bmnw.block.entity.renderer.SlidingBlastDoorRenderer;
import nl.melonstudios.bmnw.datagen.BMNWAdvancementGenerator;
import nl.melonstudios.bmnw.discard.DiscardList;
import nl.melonstudios.bmnw.effect.WPEffect;
import nl.melonstudios.bmnw.entity.MeteoriteEntity;
import nl.melonstudios.bmnw.entity.renderer.*;
import nl.melonstudios.bmnw.hardcoded.structure.Structures;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.hazard.radiation.RadiationTools;
import nl.melonstudios.bmnw.init.*;
import nl.melonstudios.bmnw.interfaces.IOnBlockAdded;
import nl.melonstudios.bmnw.interfaces.IScopeableItem;
import nl.melonstudios.bmnw.item.colorize.FireMarbleColorizer;
import nl.melonstudios.bmnw.item.misc.CoreSampleItem;
import nl.melonstudios.bmnw.misc.DistrictHolder;
import nl.melonstudios.bmnw.misc.ExcavationVein;
import nl.melonstudios.bmnw.particle.*;
import nl.melonstudios.bmnw.wifi.PacketMushroomCloud;
import nl.melonstudios.bmnw.wifi.PacketSlidingBlastDoor;
import nl.melonstudios.bmnw.wifi.PacketUpdatePressState;
import nl.melonstudios.bmnw.wifi.PacketWorkbenchCraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * BMNWs event bus.
 */
@SuppressWarnings("all")
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

        }

        /**
         * Handles custom structures
         */
        @SubscribeEvent
        public static void serverTickEventPost(ServerTickEvent.Post event) {
            while (!DELEGATE_STRUCTURES.isEmpty()) {
                try {
                    if (DELEGATE_STRUCTURES.keySet().size() > 0) {
                        Object[] array = DELEGATE_STRUCTURES.keySet().toArray();
                        if (array.length != 0) {
                            LevelAccessor level = (LevelAccessor) array[0];
                            List<ChunkPos> list = DELEGATE_STRUCTURES.remove(level);
                            for (ChunkPos pos : list) {
                                Structures.tryGenerate(level, pos, Structures.seedCache);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

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

                    RadiationTools.handleRads(entity);
                }
                if (!event.getEntity().isInWaterOrBubble() && event.getEntity() instanceof ItemEntity entity) {
                    ItemStack stack = entity.getItem();
                    float rads = HazardRegistry.getRadRegistry(stack.getItem());
                    if (rads > 0) {
                        float calculated = rads * stack.getCount() / 20;
                        BlockPos pos = new BlockPos(
                                (int) entity.getX(),
                                (int) entity.getY(),
                                (int) entity.getZ()
                        );
                        ChunkRadiationManager.handler.increaseRadiation(entity.level(), pos, calculated);
                    }
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

                float rads = nbt.getFloat("bmnw_RAD");
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
            if (!DELEGATE_STRUCTURES.containsKey(event.getLevel())) {
                DELEGATE_STRUCTURES.put(event.getLevel(), new ArrayList<>());
            }
            if (event.isNewChunk()) {
                DELEGATE_STRUCTURES.get(event.getLevel()).add(event.getChunk().getPos());
            }
        }

        @SubscribeEvent
        public static void getFOVModifier(ComputeFovModifierEvent event) {
            Player player = event.getPlayer();
            if (player.isScoping()) {
                ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (mainStack.getItem() instanceof IScopeableItem scope) {
                    event.setNewFovModifier(scope.getFOVModifier(player, mainStack, InteractionHand.MAIN_HAND));
                    return;
                }
                ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
                if (offhandStack.getItem() instanceof IScopeableItem scope) {
                    event.setNewFovModifier(scope.getFOVModifier(player, offhandStack, InteractionHand.OFF_HAND));
                }
            }
        }

        private static final Map<LevelAccessor, List<ChunkPos>> DELEGATE_STRUCTURES = new HashMap<>();

        /**
         * Handles evil item effects.
         */
        @SubscribeEvent
        public static void playerTickEventPre(PlayerTickEvent.Pre event) {
            MeteoriteEntity.spawnIfReady(event.getEntity());

            if (BMNWConfig.radiationSetting.item()) {
                Player player = event.getEntity();
                if (player.isCreative() || player.isSpectator() || player.level().isClientSide()) return;
                for (ItemStack stack : player.getInventory().items) {
                    Item item = stack.getItem();
                    if (HazardRegistry.getRadRegistry(item) > 0) {
                        RadiationTools.contaminate(player, HazardRegistry.getRadRegistry(item) / 20 * stack.getCount());
                    }
                    if (HazardRegistry.getBlindingRegistry(item)) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));
                    }
                    if (HazardRegistry.shouldSkinContact(player)) {
                        if (HazardRegistry.getBurningRegistry(item)) {
                            player.setRemainingFireTicks(20);
                        }
                        if (stack.is(BMNWTags.Items.WP)) {
                            WPEffect.inflictWP(player, 0);
                        }
                    }
                }
            }
        }
        //endregion



        //region Block events
        @SubscribeEvent
        public static void blockEventPlace(BlockEvent.EntityPlaceEvent event) {
            if (!event.getLevel().isClientSide()) ChunkRadiationManager.handler.notifyBlockChange((Level)event.getLevel(), event.getPos());
        }
        @SuppressWarnings("all")
        @SubscribeEvent
        public static void blockEventBreak(BlockEvent.BreakEvent event) {
            if (!event.getLevel().isClientSide()) ChunkRadiationManager.handler.notifyBlockChange((Level)event.getLevel(), event.getPos());
        }
        //endregion

        @SubscribeEvent
        public static void addAttributeTooltipsEvent(AddAttributeTooltipsEvent event) {
            ItemStack stack = event.getStack();
            if (DiscardList.toDiscard.contains(stack.getItem())) {
                event.addTooltipLines(Component.literal("[DEPRECATED]").withColor(0xFF0000));
            }
            if (stack.is(BMNWItems.BASE_MISSILE.get())) {
                event.addTooltipLines(Component.translatable("tooltip.bmnw.crafting_part").withColor(0x888888));
                return;
            }
            if (BMNWConfig.itemHazardInfo.id() > 0) {
                Item item = stack.getItem();
                float itemRads = HazardRegistry.getRadRegistry(item);
                if (itemRads > 0) {
                    if (BMNWConfig.itemHazardInfo.id() == 2) {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive")
                                .append(" - ").append(String.valueOf(itemRads)).append("RAD/s").withColor(0x00FF00));
                    } else {
                        event.addTooltipLines(Component.translatable("tooltip.bmnw.radioactive").withColor(0x00FF00));
                    }
                }
                float armorProtect = HazardRegistry.getArmorRadResRegistry(item);
                if (armorProtect > 0) {
                    if (BMNWConfig.itemHazardInfo.id() == 2) {
                        event.addTooltipLines(
                                Component.translatable("tooltip.bmnw.armor_rad_resistance", Math.round((armorProtect)*100))
                                        .append("%")
                                        .withColor(0xff00ff)
                        );
                    }
                }
                if (HazardRegistry.getBurningRegistry(item)) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.burning").withColor(0xFFFF00));
                }
                if (HazardRegistry.getBlindingRegistry(item)) {
                    event.addTooltipLines(Component.translatable("tooltip.b mnw.blinding").withColor(0x7777FF));
                }
                if (HazardRegistry.isWP(stack)) {
                    event.addTooltipLines(Component.translatable("tooltip.bmnw.wp").withColor(0xFFF2B2));
                }
            }
            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (HazardRegistry.enableResistanceDisplay(block) && event.getContext().flag().isAdvanced()) {
                    if (true) {
                        event.addTooltipLines(
                                Component.translatable("tooltip.bmnw.blast_resistance")
                                        .append(": ")
                                        .append(String.valueOf(block.getExplosionResistance()))
                                        .withColor(0x888888),
                                Component.translatable("tooltip.bmnw.hardness")
                                        .append(": ")
                                        .append(String.valueOf(block.defaultDestroyTime()))
                                        .withColor(0x888888)
                        );
                    } else {
                        event.addTooltipLines(
                                Component.translatable("tooltip.bmnw.alt_for_resistance")
                                        .withColor(0x888888)
                        );
                    }
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

        @SubscribeEvent
        public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (DistrictHolder.getDistrict().isClient()) {
                event.getEntity().sendSystemMessage(Component.literal("Loaded BMNW version " + BMNW.getVersionStr()));
                if (!ModList.get().isLoaded("ctm")) {
                    event.getEntity().sendSystemMessage(Component.translatable("Note: CTM is not installed, some textures may look disappealing"));
                }
            } else {
                event.getEntity().sendSystemMessage(Component.literal("Joined server with BMNW version " + BMNW.getVersionStr()));
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
        @OnlyIn(Dist.CLIENT)
        private static <T extends BlockEntity, V extends T> void registerBlockEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers event,
                                                                                                     Supplier<BlockEntityType<V>> type,
                                                                                                     BlockEntityRendererProvider<T> renderer) {
            event.registerBlockEntityRenderer(type.get(), renderer);
        }

        @OnlyIn(Dist.CLIENT)
        private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            registerBlockEntityRenderingHandler(event, BMNWBlockEntities.HATCH, HatchRenderer::new);
            registerBlockEntityRenderingHandler(event, BMNWBlockEntities.SLIDING_BLAST_DOOR, SlidingBlastDoorRenderer::new);
            registerBlockEntityRenderingHandler(event, BMNWBlockEntities.PRESS, PressRenderer::new);
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

            registerEntityRenderingHandler(event, BMNWEntityTypes.METEORITE, MeteoriteRenderer::new);

            registerEntityRenderingHandler(event, BMNWEntityTypes.SIMPLE_BULLET, SimpleBulletRenderer::new);
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
            event.registerSpriteSet(BMNWParticleTypes.FIRE_TRAIL.get(), FireTrailParticle.Provider::new);
            event.registerSpriteSet(BMNWParticleTypes.MUSHROOM_CLOUD.get(), MushroomCloudParticle.Provider::new);
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

        @SubscribeEvent
        public static void registerNetwork(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(
                    PacketMushroomCloud.TYPE,
                    PacketMushroomCloud.STREAM_CODEC,
                    PacketMushroomCloud::handle
            );
            registrar.playToClient(
                    PacketSlidingBlastDoor.TYPE,
                    PacketSlidingBlastDoor.STREAM_CODEC,
                    PacketSlidingBlastDoor::handle
            );
            registrar.playToClient(
                    PacketUpdatePressState.TYPE,
                    PacketUpdatePressState.STREAM_CODEC,
                    PacketUpdatePressState::handle
            );
            registrar.playToServer(
                    PacketWorkbenchCraft.TYPE,
                    PacketWorkbenchCraft.STREAM_CODEC,
                    PacketWorkbenchCraft::handle
            );
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerColorHandlersItem(RegisterColorHandlersEvent.Item event) {
            event.register(new FireMarbleColorizer(), BMNWItems.FIRE_MARBLE);
        }
    }
}
