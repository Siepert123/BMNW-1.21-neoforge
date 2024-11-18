package com.siepert.bmnw;

import com.mojang.logging.LogUtils;
import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.block.entity.ModBlockEntities;
import com.siepert.bmnw.critereon.ModAdvancementTriggers;
import com.siepert.bmnw.effect.ModEffects;
import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.item.ModItems;
import com.siepert.bmnw.item.components.ModDataComponents;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModSounds;
import com.siepert.bmnw.misc.ModTabs;
import com.siepert.bmnw.particle.ModParticleTypes;
import com.siepert.bmnw.radiation.ShieldingValues;
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
            "Now with extra visuals!"
    };

    public BMNW(IEventBus modEventBus, @Nonnull ModContainer ignoredModContainer) {
        modEventBus.addListener(this::commonSetup);

        LOGGER.debug("BMNW loader");
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModTabs.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModParticleTypes.register(modEventBus);
        ModSounds.register(modEventBus);
        ModAdvancementTriggers.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        LOGGER.info(splashes[new Random().nextInt(splashes.length)]);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ShieldingValues.initialize();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
