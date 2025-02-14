package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Handles world radiation.
 * My previous system was nice, but the server did not like it.
 * Thus, I took some 'inspiration' from HBM, if you know what I mean
 *
 * @author HBM the bobcat
 */
public abstract class ChunkRadiationHandler {
    public static MinecraftServer server;

    public abstract void updateSystem();
    public abstract float getRadiation(Level level, BlockPos pos);
    public abstract void setRadiation(Level level, BlockPos pos, float rads);
    public abstract void increaseRadiation(Level level, BlockPos pos, float rads);
    public abstract void decreaseRadiation(Level level, BlockPos pos, float rads);
    public abstract void clearSystem(Level level);

    public void onWorldLoad(LevelEvent.Load event) {}
    public void onWorldUnload(LevelEvent.Unload event) {}
    public void onWorldTick(ServerTickEvent event) {}

    public void onChunkLoad(ChunkDataEvent.Load event) {}
    public void onChunkSave(ChunkDataEvent.Save event) {}
    public void onChunkUnload(ChunkEvent.Unload event) {}

    public void handleWorldDestruction() {}
}
