package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.entity.*;
import nl.melonstudios.bmnw.weapon.RadiationLingerEntity;
import nl.melonstudios.bmnw.weapon.explosion.ExplosionHelperEntity;
import nl.melonstudios.bmnw.weapon.missile.entity.CustomizableMissileEntity;
import nl.melonstudios.bmnw.weapon.nuke.FallingBombEntity;
import nl.melonstudios.bmnw.weapon.torpedo.TorpedoEntity;

public class BMNWEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "bmnw");

    public static final DeferredHolder<EntityType<?>, EntityType<ExplosionHelperEntity>> EXPLOSION_HELPER = ENTITY_TYPES.register(
            "explosion_helper",
            () -> EntityType.Builder.<ExplosionHelperEntity>of(ExplosionHelperEntity::new, MobCategory.MISC)
                    .clientTrackingRange(2048)
                    .sized(1.0F, 1.0F)
                    .noSummon()
                    .noSave()
                    .setShouldReceiveVelocityUpdates(false)
                    .build("bmnw:explosion_helper")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RadiationLingerEntity>> RADIATION_LINGER = ENTITY_TYPES.register(
            "radiation_linger",
            () -> EntityType.Builder.<RadiationLingerEntity>of(RadiationLingerEntity::new, MobCategory.MISC)
                    .clientTrackingRange(16)
                    .sized(1.0F, 1.0F)
                    .setShouldReceiveVelocityUpdates(false)
                    .build("bmnw:radiation_linger")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<FallingBombEntity>> FALLING_BOMB = ENTITY_TYPES.register(
            "falling_bomb",
            () -> EntityType.Builder.<FallingBombEntity>of(FallingBombEntity::new, MobCategory.MISC)
                    .clientTrackingRange(1024)
                    .sized(1.0F, 1.0F)
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(false)
                    .build("bmnw:falling_bomb")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<BlockDebrisEntity>> BLOCK_DEBRIS = ENTITY_TYPES.register(
            "block_debris",
            () -> EntityType.Builder.<BlockDebrisEntity>of(BlockDebrisEntity::new, MobCategory.MISC)
                    .clientTrackingRange(512)
                    .sized(0.5f, 0.5f)
                    .build("bmnw:block_debris")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<MultiblockDebrisEntity>> MULTIBLOCK_DEBRIS = ENTITY_TYPES.register(
            "multiblock_debris",
            () -> EntityType.Builder.<MultiblockDebrisEntity>of(MultiblockDebrisEntity::new, MobCategory.MISC)
                    .clientTrackingRange(512)
                    .sized(1.5F, 1.5F)
                    .build("bmnw:multiblock_debris")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<RubbleEntity>> RUBBLE = ENTITY_TYPES.register(
            "rubble",
            () -> EntityType.Builder.<RubbleEntity>of(RubbleEntity::new, MobCategory.MISC)
                    .clientTrackingRange(512)
                    .sized(1, 1)
                    .build("bmnw:rubble")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<MeteoriteEntity>> METEORITE = ENTITY_TYPES.register(
            "meteorite",
            () -> EntityType.Builder.<MeteoriteEntity>of(MeteoriteEntity::new, MobCategory.MISC)
                    .sized(1, 1)
                    .clientTrackingRange(2048)
                    .build("bmnw:meteorite")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<LavaEjectionEntity>> LAVA_EJECTION = ENTITY_TYPES.register(
            "lava_ejection",
            () -> EntityType.Builder.<LavaEjectionEntity>of(LavaEjectionEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(1024)
                    .build("bmnw:lava_ejection")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SimpleBulletEntity>> SIMPLE_BULLET = ENTITY_TYPES.register(
            "simple_bullet",
            () -> EntityType.Builder.<SimpleBulletEntity>of(SimpleBulletEntity::new, MobCategory.MISC)
                    .sized(0.125f, 0.125f)
                    .noSummon()
                    .build("bmnw:simple_bullet")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<TorpedoEntity>> TORPEDO = ENTITY_TYPES.register(
            "torpedo",
            () -> EntityType.Builder.<TorpedoEntity>of(TorpedoEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .noSummon()
                    .build("bmnw:torpedo")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<CustomizableMissileEntity>> CUSTOMIZABLE_MISSILE = ENTITY_TYPES.register(
            "customizable_missile",
            () -> EntityType.Builder.<CustomizableMissileEntity>of(CustomizableMissileEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .eyeHeight(0.5F)
                    .noSummon()
                    .clientTrackingRange(2048)
                    .build("bmnw:customizable_missile")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
