package nl.melonstudios.bmnw;

import net.minecraft.server.level.ServerLevel;
import nl.melonstudios.bmnw.hardcoded.structure.*;
import nl.melonstudios.bmnw.hardcoded.structure.coded.StructureAncientMuseum;
import nl.melonstudios.bmnw.hardcoded.structure.coded.StructureBrickBuilding;
import com.mojang.logging.LogUtils;
import nl.melonstudios.bmnw.block.BMNWBlocks;
import nl.melonstudios.bmnw.block.entity.BMNWBlockEntities;
import nl.melonstudios.bmnw.critereon.BMNWAdvancementTriggers;
import nl.melonstudios.bmnw.effect.BMNWEffects;
import nl.melonstudios.bmnw.entity.BMNWEntityTypes;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationHandler;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.item.BMNWItems;
import nl.melonstudios.bmnw.item.components.BMNWDataComponents;
import nl.melonstudios.bmnw.misc.*;
import nl.melonstudios.bmnw.particle.BMNWParticleTypes;
import nl.melonstudios.bmnw.radiation.ShieldingValues;
import nl.melonstudios.bmnw.recipe.BMNWRecipeSerializers;
import nl.melonstudios.bmnw.recipe.BMNWRecipeTypes;
import nl.melonstudios.bmnw.hardcoded.structure.coded.StructureRadioAntenna;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Random;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BMNW.MODID)
public class BMNW {
    public static final String MODID = "bmnw";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String[] splashes = {
            "Bunkers!", "Machines!", "Nuclear Weapons!",
            "Have you tried Create Legacy?",
            "Have you tried MelonLoader?",
            "Radiation free!",
            "Now with extra visuals!",
            "[Brick Bomb]!",
            "Uranium Sandwich!",
            "Block of Uranium-235!"
    };

    public BMNW(IEventBus modEventBus, @Nonnull ModContainer ignoredModContainer, Dist dist) {
        DistrictHolder.setDistrict(dist);
        modEventBus.addListener(this::commonSetup);

        LOGGER.debug("BMNW loader");
        BMNWBlocks.register(modEventBus);
        BMNWBlockEntities.register(modEventBus);
        BMNWItems.register(modEventBus);
        BMNWRecipeTypes.register(modEventBus);
        BMNWRecipeSerializers.register(modEventBus);
        BMNWTabs.register(modEventBus);
        BMNWDataComponents.register(modEventBus);
        BMNWAttachments.register(modEventBus);
        BMNWEntityTypes.register(modEventBus);
        BMNWEffects.register(modEventBus);
        BMNWParticleTypes.register(modEventBus);
        BMNWSounds.register(modEventBus);
        BMNWAdvancementTriggers.register(modEventBus);

        MidnightConfig.init("bmnw", BMNWConfig.class);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        //Add radiation listeners
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onWorldLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onWorldUnload);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkDataLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkSave);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::updateSystem);

        LOGGER.info(splashes[new Random().nextInt(splashes.length)]);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ShieldingValues.initialize();
        ExcavationVein.initialize();
        Books.registerBooks();
        HazardRegistry.register();

        Structures.registerStructure("bmnw:radio_antenna",
                new StructureData(new StructureRadioAntenna(), new StructureSpawningLogic(0.001f)
                        .setBiomeConstraint(biome -> biome.is(BMNWTags.Biomes.HAS_RADIO_ANTENNA))
                        .setSalt("bmnw:radio_antenna".hashCode())
                        .setAllowMultipleStructures(false)));
        Structures.registerStructure("bmnw:brick_building",
                new StructureData(new StructureBrickBuilding(), new StructureSpawningLogic(0.0001f)
                        .setBiomeConstraint(biome -> biome.is(BMNWTags.Biomes.HAS_BRICK_BUILDING))
                        .setSalt("bmnw:brick_building".hashCode()))
                        .addBlockModifier(new ConcreteBricksDecayModifier(0.3f))
                        .addBlockModifier(new BlockModifierGlassPaneDecay()));
        Structures.registerStructure("bmnw:ancient_museum",
                new StructureData(new StructureAncientMuseum(), new StructureSpawningLogic(0.0005f)
                        .setSalt("bmnw:ancient_museum".hashCode())));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        BMNWServerConfig.load(event.getServer());

        ChunkRadiationHandler.server = event.getServer();

        {
            Iterable<ServerLevel> levels = event.getServer().getAllLevels();
            boolean first = true;
            for (ServerLevel level : levels) {
                if (first) Structures.seedCache = level.getSeed();
                else {
                    if (level.getSeed() != Structures.seedCache) {
                        Structures.LOGGER.warn("Seed mismatch in levels: got {} while original was {}", level.getSeed(), Structures.seedCache);
                    }
                }
                first = false;
            }
            Structures.validCache = true;
        }

        Commands commands = event.getServer().getCommands();
        BMNWCommands.register(commands);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        Structures.validCache = false;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            ChunkRadiationManager.handler.clearSystem(level);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static ResourceLocation namespace(String key) {
        return ResourceLocation.fromNamespaceAndPath("bmnw", key);
    }

    public static final class Constants {
        private Constants() {
            throw new Error();
        }
        public static int evil_fog_rads = 100;
        public static int evil_fog_chance = 20;
    }
}
