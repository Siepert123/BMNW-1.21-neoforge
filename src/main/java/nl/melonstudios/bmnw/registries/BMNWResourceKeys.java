package nl.melonstudios.bmnw.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.DistrictHolder;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;
import nl.melonstudios.bmnw.weapon.missile.registry.*;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;

public class BMNWResourceKeys {
    //region Custom code registries
    public static final ResourceKey<Registry<NukeType>> NUKE_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(BMNW.namespace("nuke_type"));
    public static final Registry<NukeType> NUKE_TYPE_REGISTRY =
            new RegistryBuilder<>(NUKE_TYPE_REGISTRY_KEY)
                    .sync(true)
                    .maxId(256)
                    .create();


    public static final ResourceKey<Registry<MissileThruster>> MISSILE_THRUSTER_REGISTRY_KEY =
            ResourceKey.createRegistryKey(BMNW.namespace("missile/thrusters"));
    public static final Registry<MissileThruster> MISSILE_THRUSTER_REGISTRY =
            new RegistryBuilder<>(MISSILE_THRUSTER_REGISTRY_KEY)
                    .sync(true)
                    .onAdd((registry, id, key, value) -> {
                        DistrictHolder.clientOnly(() -> MissileSystem::createThrusterModel, key.location(), value);
                    })
                    .create();

    public static final ResourceKey<Registry<MissileFins>> MISSILE_FINS_REGISTRY_KEY =
            ResourceKey.createRegistryKey(BMNW.namespace("missile/fins"));
    public static final Registry<MissileFins> MISSILE_FINS_REGISTRY =
            new RegistryBuilder<>(MISSILE_FINS_REGISTRY_KEY)
                    .sync(true)
                    .onAdd((registry, id, key, value) -> {
                        DistrictHolder.clientOnly(() -> MissileSystem::createFinsModel, key.location(), value);
                    })
                    .create();

    public static final ResourceKey<Registry<MissileFuselage>> MISSILE_FUSELAGE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(BMNW.namespace("missile/fuselages"));
    public static final Registry<MissileFuselage> MISSILE_FUSELAGE_REGISTRY =
            new RegistryBuilder<>(MISSILE_FUSELAGE_REGISTRY_KEY)
                    .sync(true)
                    .onAdd((registry, id, key, value) -> {
                        DistrictHolder.clientOnly(() -> MissileSystem::createFuselageModel, key.location(), value);
                    })
                    .create();

    public static final ResourceKey<Registry<MissileWarhead>> MISSILE_WARHEAD_REGISTRY_KEY =
            ResourceKey.createRegistryKey(BMNW.namespace("missile/warheads"));
    public static final Registry<MissileWarhead> MISSILE_WARHEAD_REGISTRY =
            new RegistryBuilder<>(MISSILE_WARHEAD_REGISTRY_KEY)
                    .sync(true)
                    .onAdd((registry, id, key, value) -> {
                        DistrictHolder.clientOnly(() -> MissileSystem::createWarheadModel, key.location(), value);
                    })
                    .create();


    public static void registerRegistries(NewRegistryEvent event) {
        event.register(NUKE_TYPE_REGISTRY);

        event.register(MISSILE_THRUSTER_REGISTRY);
        event.register(MISSILE_FINS_REGISTRY);
        event.register(MISSILE_FUSELAGE_REGISTRY);
        event.register(MISSILE_WARHEAD_REGISTRY);
    }
    //endregion

    //region Data pack registries
    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {

    }
    //endregion
}
