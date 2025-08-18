package nl.melonstudios.bmnw;

import com.mojang.logging.LogUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import nl.melonstudios.bmnw.cfg.BMNWClientConfig;
import nl.melonstudios.bmnw.cfg.BMNWCommonConfig;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.hardcoded.structure.*;
import nl.melonstudios.bmnw.hardcoded.structure.coded.*;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationHandler;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.init.*;
import nl.melonstudios.bmnw.interfaces.IOpensCatwalkRails;
import nl.melonstudios.bmnw.logistics.cables.CableNetManager;
import nl.melonstudios.bmnw.logistics.pipes.PipeNetManager;
import nl.melonstudios.bmnw.misc.*;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.screen.*;
import nl.melonstudios.bmnw.screen.nuke.CaseohScreen;
import nl.melonstudios.bmnw.screen.nuke.LittleBoyScreen;
import nl.melonstudios.bmnw.weapon.explosion.LevelActiveExplosions;
import nl.melonstudios.bmnw.weapon.missile.registry.BMNWMissileParts;
import nl.melonstudios.bmnw.weapon.nuke.BMNWNukeTypes;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

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
            "Block of Uranium-235!",
            "The PlayStation can produce mind-boggling effects!",
    };

    public static boolean checkApril1st() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.APRIL) {
            return calendar.get(Calendar.DAY_OF_MONTH) == 1;
        }
        return false;
    }

    private static final boolean enforceMemz = checkApril1st();
    public static final boolean memz = enforceMemz || RandomSource.create().nextInt(100) == 69;
    public static final int memzArguments = Math.abs(RandomSource.create().nextInt());
    public static final String randomSplash = splashes[memzArguments % splashes.length];

    public static String getVersionStr() {
        return Objects.requireNonNull(versionStr);
    }

    private static String versionStr;
    private static ModContainer modContainer;
    public static final boolean autoDecidedMemoryMinimization;

    private static int hintIndex = 0;
    private static final ArrayList<String> hints = new ArrayList<>();
    @OnlyIn(Dist.CLIENT)
    public static String getRandomHint() {
        return hints.get(RandomHelper.nextInt(System.currentTimeMillis() / 2000, 3, hints.size()));
    }

    static {
        IOpensCatwalkRails.init();
        autoDecidedMemoryMinimization = Runtime.getRuntime().maxMemory() < Runtime.getRuntime().availableProcessors() * 1073741824L;
    }

    public BMNW(IEventBus modEventBus, @Nonnull ModContainer modContainer, Dist dist) {
        NeoForgeMod.enableMilkFluid();

        DistrictHolder.setDistrict(dist);
        modEventBus.addListener(this::commonSetup);

        BMNW.modContainer = modContainer;
        versionStr = modContainer.getModInfo().getVersion().toString();

        NeoForge.EVENT_BUS.register(this);

        LOGGER.debug("BMNW loader");

        modContainer.registerConfig(ModConfig.Type.SERVER, BMNWServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BMNWClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, BMNWCommonConfig.SPEC);

        modEventBus.addListener(BMNWClientConfig::onLoad);

        BMNWBlocks.register(modEventBus);
        BMNWBlockEntities.register(modEventBus);
        BMNWItems.register(modEventBus);
        BMNWFluids.register(modEventBus);
        BMNWTabs.register(modEventBus);
        BMNWDataComponents.register(modEventBus);
        BMNWAttachments.register(modEventBus);
        BMNWEntityTypes.register(modEventBus);
        BMNWEffects.register(modEventBus);
        BMNWParticleTypes.register(modEventBus);
        BMNWSounds.register(modEventBus);
        BMNWAdvancementTriggers.register(modEventBus);
        BMNWMenuTypes.register(modEventBus);
        BMNWRecipes.register(modEventBus);

        BMNWNukeTypes.register(modEventBus);
        BMNWMissileParts.register(modEventBus);

        DistrictHolder.clientOnly(() -> BMNW::clientInit);

        modEventBus.addListener(this::addCreative);

        //Add radiation listeners
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onWorldLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onWorldUnload);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkDataLoad);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkSave);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(ChunkRadiationManager::updateSystem);

        modEventBus.addListener(BMNWResourceKeys::registerRegistries);
        modEventBus.addListener(BMNWResourceKeys::registerDataPackRegistries);

        if (dist.isClient()) {
            clientModEventBusPassthrough(modEventBus);
        }

        if (dist.isClient()) {
            modEventBus.addListener(this::registerMenuScreens);
        }

        LOGGER.info(randomSplash);

        if (dist.isClient()) {
            try (
                    InputStream in = getJarInputStream("hints.txt");
                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader buf = new BufferedReader(reader)
            ) {
                buf.lines().filter((s) -> !s.isBlank()).forEach(hints::add);
            } catch (IOException e) {
                hints.clear();
                hints.add("Error loading hints:$" + e.getClass().getName() + ":$" + e.getLocalizedMessage());
            } finally {
                LOGGER.debug("Loaded {} hints:", hints.size());
                hints.forEach(LOGGER::debug);
            }
        }
    }

    private static void clientInit() {
        BMNWPartialModels.init();
    }

    private static void clientModEventBusPassthrough(IEventBus modEventBus) {
        clientMEB(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientMEB(IEventBus modEventBus) {
        modEventBus.addListener(BMNW::onBakingComplete);
        modEventBus.addListener(BMNW::registerAdditionalModels);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Runtime runtime = Runtime.getRuntime();
        long memTotal = runtime.totalMemory();
        long memFree = runtime.freeMemory();
        long memMax = runtime.maxMemory();
        LOGGER.debug("##Runtime Info Dump##\nCPU cores: {}\nTotal memory: {} bytes ({}GB)\nFree memory: {} bytes ({} GB)\nMax memory: {} bytes ({} GB)",
                runtime.availableProcessors(),
                memTotal, Library.toGB(memTotal, 2),
                memFree, Library.toGB(memFree, 2),
                memMax, Library.toGB(memMax, 2)
        );
        LOGGER.debug("Minimizing memory usage: {} (auto-decided: {})",
                BMNWCommonConfig.minimizeMemoryUsage().value(BMNW.autoDecidedMemoryMinimization),
                BMNWCommonConfig.minimizeMemoryUsage().isMaybe()
        );

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
        Structures.registerStructure("bmnw:missile_silo",
                new StructureData(new StructureMissileSilo(), new StructureSpawningLogic(0.0001f)
                        .setBiomeConstraint(biome -> biome.is(BMNWTags.Biomes.HAS_MISSILE_SILO))
                        .setSalt("bmnw:missile_silo".hashCode()))
                        .addBlockModifier(new ConcreteBricksDecayModifier(0.2f)));
        Structures.registerStructure("bmnw:dud",
                new StructureData(new StructureDud(), new StructureSpawningLogic(0.0005f)
                        .setBiomeConstraint(biome -> !biome.is(BiomeTags.IS_OCEAN) && !biome.is(BiomeTags.IS_RIVER) && biome.is(BiomeTags.IS_OVERWORLD))
                        .setSalt("bmnw:dud".hashCode())
                        .setAllowMultipleStructures(false)));
        Structures.registerStructure("bmnw:bunker",
                new StructureData(new StructureBunker(), new StructureSpawningLogic(0.0001F)
                        .setBiomeConstraint(biome -> !biome.is(BiomeTags.IS_OCEAN) && !biome.is(BiomeTags.IS_RIVER) && biome.is(BiomeTags.IS_OVERWORLD))
                        .setSalt("bmnw:dud".hashCode()))
                        .addBlockModifier(new ConcreteBricksDecayModifier(0.1F)));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(BMNWMenuTypes.PRESS.get(), PressScreen::new);
        event.register(BMNWMenuTypes.ALLOY_BLAST_FURNACE.get(), AlloyFurnaceScreen::new);
        event.register(BMNWMenuTypes.BUILDERS_FURNACE.get(), BuildersFurnaceScreen::new);
        event.register(BMNWMenuTypes.WORKBENCH.get(), WorkbenchScreen::new);

        event.register(BMNWMenuTypes.COMBUSTION_ENGINE.get(), CombustionEngineScreen::new);
        event.register(BMNWMenuTypes.INDUSTRIAL_HEATER.get(), IndustrialHeaterScreen::new);

        event.register(BMNWMenuTypes.ENERGY_STORAGE.get(), EnergyStorageScreen::new);
        event.register(BMNWMenuTypes.FLUID_BARREL.get(), FluidBarrelScreen::new);

        event.register(BMNWMenuTypes.LITTLE_BOY.get(), LittleBoyScreen::new);
        event.register(BMNWMenuTypes.CASEOH.get(), CaseohScreen::new);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        BMNWServerCfg.load(event.getServer());

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

                LevelActiveExplosions.createIfNecessary(level);
                PipeNetManager.createIfNecessary(level);
                CableNetManager.createIfNecessary(level);
            }
            Structures.validCache = true;
        }

        event.getServer().addTickable(LevelActiveExplosions::tick);

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

    @SubscribeEvent
    public void serverStopped(ServerStoppedEvent event) {
        PipeNetManager.clear(event);
        CableNetManager.clear(event);
        LevelActiveExplosions.clear(event);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static ResourceLocation namespace(String key) {
        return ResourceLocation.fromNamespaceAndPath("bmnw", key);
    }
    public static VoxelShape shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Shapes.box(x1 / 16, y1 / 16, z1 / 16, x2 / 16, y2 / 16, z2 / 16);
    }

    public static final class Constants {
        private Constants() {
            throw new Error();
        }
        public static int evil_fog_rads = 100;
        public static int evil_fog_chance = 20;
    }

    public static void onBakingComplete(ModelEvent.BakingCompleted event) {
        PartialModel.populateOnInit = true;
        Map<ModelResourceLocation, BakedModel> models = event.getModels();

        for (PartialModel partial : PartialModel.ALL.values()) {
            partial.bakedModel = models.get(partial.modelLocation());
        }
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        PartialModel.ALL.keySet().forEach(event::register);
    }

    public static InputStream getJarInputStream(String path) throws IOException {
        return Files.newInputStream(modContainer.getModInfo().getOwningFile().getFile().findResource(path));
    }

    public static CompoundTag getJarNbt(String path) {
        try (InputStream stream = getJarInputStream(path)) {
            return NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            return new CompoundTag();
        }
    }
}
