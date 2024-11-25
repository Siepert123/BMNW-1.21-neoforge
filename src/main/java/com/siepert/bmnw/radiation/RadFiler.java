package com.siepert.bmnw.radiation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RadFiler {
    private static final Logger LOGGER = LogManager.getLogger("RadFiler");
    private RadFiler() {}

    public static void rerun(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
        }
    }
}
