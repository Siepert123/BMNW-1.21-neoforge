package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nl.melonstudios.bmnw.misc.BMNWConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class ChunkRadiationManager {
    public static final ChunkRadiationHandler handler = new ChunkRadiationHandlerPRISM();

    public static void onWorldLoad(LevelEvent.Load event) {
        if (BMNWConfig.radiationSetting.chunk()) handler.onWorldLoad(event);
    }

    public static void onWorldUnload(LevelEvent.Unload event) {
        if (BMNWConfig.radiationSetting.chunk()) handler.onWorldUnload(event);
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (BMNWConfig.radiationSetting.chunk() && event.getLevel() != null) handler.onChunkLoad(event);
    }
    public static void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (BMNWConfig.radiationSetting.chunk() && event.getLevel() != null) handler.onChunkDataLoad(event);
    }

    public static void onChunkSave(ChunkDataEvent.Save event) {
        if (BMNWConfig.radiationSetting.chunk() && event.getLevel() != null) handler.onChunkSave(event);
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (BMNWConfig.radiationSetting.chunk() && event.getLevel() != null) handler.onChunkUnload(event);
    }

    static int eggTimer = 0;

    public static void updateSystem(ServerTickEvent.Post event) {
        if (BMNWConfig.radiationSetting.chunk()) {
            eggTimer++;

            if (eggTimer >= 20) {
                handler.updateSystem();
                eggTimer = 0;
            }

            handler.handleWorldDestruction();

            handler.onWorldTick(event);
        }
    }

    private static final Map<ResourceLocation, Supplier<ChunkRadiationHandler>> handler_registry = new HashMap<>();

    public static void registerHandler(String id, Supplier<ChunkRadiationHandler> handler) {
        ResourceLocation rsl = ResourceLocation.parse(id);
        if (handler_registry.containsKey(rsl)) {
            throw new IllegalStateException("Cannot double register radiation handler: " + rsl);
        }
        handler_registry.put(rsl, handler);
    }

    public static void setHandler(ResourceLocation id) {

    }

    {
        registerHandler("bmnw:empty",   ChunkRadiationHandlerBlank::new);
        registerHandler("bmnw:simple",  ChunkRadiationHandlerSimple::new);
        registerHandler("bmnw:three_d", ChunkRadiationHandler3D::new);
        registerHandler("bmnw:prism",   ChunkRadiationHandlerPRISM::new);
    }
}