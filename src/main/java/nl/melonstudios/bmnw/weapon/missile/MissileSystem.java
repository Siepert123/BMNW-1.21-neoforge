package nl.melonstudios.bmnw.weapon.missile;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.misc.PartialModel;
import nl.melonstudios.bmnw.registries.BMNWResourceKeys;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFins;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFuselage;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileThruster;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileWarhead;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class MissileSystem {
    public static Registry<MissileThruster> registry_MissileThruster() {
        return BMNWResourceKeys.MISSILE_THRUSTER_REGISTRY;
    }
    public static Registry<MissileFins> registry_MissileFins() {
        return BMNWResourceKeys.MISSILE_FINS_REGISTRY;
    }
    public static Registry<MissileFuselage> registry_MissileFuselage() {
        return BMNWResourceKeys.MISSILE_FUSELAGE_REGISTRY;
    }
    public static Registry<MissileWarhead> registry_MissileWarhead() {
        return BMNWResourceKeys.MISSILE_WARHEAD_REGISTRY;
    }

    public static void createThrusterModel(ResourceLocation key, MissileThruster value) {
        Client.createThrusterModel(key, value);
    }
    public static void createFinsModel(ResourceLocation key, MissileFins value) {
        Client.createFinsModel(key, value);
    }
    public static void createFuselageModel(ResourceLocation key, MissileFuselage value) {
        Client.createFuselageModel(key, value);
    }
    public static void createWarheadModel(ResourceLocation key, MissileWarhead value) {
        Client.createWarheadModel(key, value);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Client {
        private static final Logger LOGGER = LogManager.getLogger("MissileSystem/Client");

        private static final HashMap<MissileThruster, PartialModel> THRUSTER_MODELS = new HashMap<>();
        private static final HashMap<MissileFins, PartialModel> FINS_MODELS = new HashMap<>();
        private static final HashMap<MissileFuselage, PartialModel> FUSELAGE_MODELS = new HashMap<>();
        private static final HashMap<MissileWarhead, PartialModel> WARHEAD_MODELS = new HashMap<>();
        public static ModelResourceLocation createMRL(String type, ResourceLocation id) {
            String namespace = id.getNamespace();
            String path = id.getPath();
            return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(namespace, "missile/" + type + "/" + path));
        }

        private static void createThrusterModel(ResourceLocation key, MissileThruster value) {
            LOGGER.info("Creating thruster model for {}", key);
            ModelResourceLocation model = createMRL("thruster", key);
            THRUSTER_MODELS.put(value, PartialModel.of(model));
        }
        private static void createFinsModel(ResourceLocation key, MissileFins value) {
            LOGGER.info("Creating fins model for {}", key);
            ModelResourceLocation model = createMRL("fins", key);
            FINS_MODELS.put(value, PartialModel.of(model));
        }
        private static void createFuselageModel(ResourceLocation key, MissileFuselage value) {
            LOGGER.info("Creating fuselage model for {}", key);
            ModelResourceLocation model = createMRL("fuselage", key);
            FUSELAGE_MODELS.put(value, PartialModel.of(model));
        }
        private static void createWarheadModel(ResourceLocation key, MissileWarhead value) {
            LOGGER.info("Creating warhead model for {}", key);
            ModelResourceLocation model = createMRL("warhead", key);
            WARHEAD_MODELS.put(value, PartialModel.of(model));
        }

        //There is definitely a better way to do this
        public static BakedModel getOnTheFly(MissileThruster thruster) {
            return THRUSTER_MODELS.get(thruster).loadAndGet();
        }
        public static BakedModel getOnTheFly(MissileFins fins) {
            return FINS_MODELS.get(fins).loadAndGet();
        }
        public static BakedModel getOnTheFly(MissileFuselage fuselage) {
            return FUSELAGE_MODELS.get(fuselage).loadAndGet();
        }
        public static BakedModel getOnTheFly(MissileWarhead warhead) {
            return WARHEAD_MODELS.get(warhead).loadAndGet();
        }
    }
}
