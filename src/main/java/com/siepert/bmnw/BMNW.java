package com.siepert.bmnw;

import com.mojang.logging.LogUtils;
import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.critereon.BMNWAdvancementTriggers;
import com.siepert.bmnw.effect.BMNWEffects;
import com.siepert.bmnw.entity.BMNWEntityTypes;
import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.item.BMNWItems;
import com.siepert.bmnw.item.components.BMNWDataComponents;
import com.siepert.bmnw.misc.*;
import com.siepert.bmnw.particle.BMNWParticleTypes;
import com.siepert.bmnw.radiation.RadiationManager;
import com.siepert.bmnw.radiation.ShieldingValues;
import com.siepert.bmnw.recipe.BMNWRecipeSerializers;
import com.siepert.bmnw.recipe.BMNWRecipeTypes;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

        LOGGER.info(splashes[new Random().nextInt(splashes.length)]);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ShieldingValues.initialize();
        ExcavationVein.initialize();
        Books.registerBooks();
        HazardRegistry.register();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RadiationManager.create(event.getServer());

        Commands commands = event.getServer().getCommands();
        BMNWCommands.register(commands);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        RadiationManager.delete(event.getServer());
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
}
