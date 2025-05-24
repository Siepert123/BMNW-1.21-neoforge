package nl.melonstudios.bmnw.init;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BMNWServerCfg {
    public static void load(MinecraftServer server) {
        File file = server.getFile("config/bmnw.cfg").toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load bmnw.cfg", e);
            }
            loadDefaults(file);
        } else {
            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject json = gson.fromJson(reader, new TypeToken<>(){});
                radiation_system_id = ResourceLocation.parse(json.get("radiation_system_id").getAsString());
            } catch (IOException e) {
                file.delete();
                throw new RuntimeException(e);
            }
        }

        init();
    }

    private static void init() {
        ChunkRadiationManager.setHandler(radiation_system_id);
    }

    private static void loadDefaults(File file) {
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement radiation_id = new JsonPrimitive("bmnw:simple");
            JsonObject json = new JsonObject();
            json.add("radiation_system_id", radiation_id);
            gson.toJson(json, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ResourceLocation radiation_system_id = ResourceLocation.fromNamespaceAndPath("bmnw", "simple");
}
