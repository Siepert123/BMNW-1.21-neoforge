package com.siepert.bmnw.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class BMNWEventBus {

    @EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.GAME)
    public static class GameEventBus {
        @SubscribeEvent
        public static void serverTickEventPre(ServerTickEvent.Pre event) {

        }

        @SubscribeEvent
        public static void serverTickEventPost(ServerTickEvent.Post event) {

        }

        @SubscribeEvent
        public static void entityTickEventPre(EntityTickEvent.Pre event) {

        }

        @SubscribeEvent
        public static void entityTickEventPost(EntityTickEvent.Post event) {

        }
    }

    //@EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {

    }
}
