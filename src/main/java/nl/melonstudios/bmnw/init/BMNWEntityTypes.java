package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.entity.*;

public class BMNWEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "bmnw");

    public static final DeferredHolder<EntityType<?>, EntityType<BlockDebrisEntity>> BLOCK_DEBRIS = ENTITY_TYPES.register(
            "block_debris",
            () -> EntityType.Builder.of(BlockDebrisEntity::new, MobCategory.MISC)
                    .clientTrackingRange(128)
                    .sized(0.5f, 0.5f)
                    .build("bmnw:block_debris")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<NuclearChargeEntity>> NUCLEAR_CHARGE = ENTITY_TYPES.register(
            "nuclear_charge",
            () -> EntityType.Builder.of(NuclearChargeEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(256)
                    .build("bmnw:nuclear_charge")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DudEntity>> DUD = ENTITY_TYPES.register(
            "dud",
            () -> EntityType.Builder.of(DudEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(256)
                    .build("bmnw:dud")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LittleBoyEntity>> LITTLE_BOY = ENTITY_TYPES.register(
            "little_boy",
            () -> EntityType.Builder.of(LittleBoyEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:little_boy")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<CaseohEntity>> CASEOH = ENTITY_TYPES.register(
            "caseoh",
            () -> EntityType.Builder.of(CaseohEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(1024)
                    .build("bmnw:caseoh")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<AntiMissileMissileEntity>> ANTI_MISSILE_MISSILE = ENTITY_TYPES.register(
            "anti_missile_missile",
            () -> EntityType.Builder.of(AntiMissileMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:anti_missile_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<ExampleMissileEntity>> EXAMPLE_MISSILE = ENTITY_TYPES.register(
            "example_missile",
            () -> EntityType.Builder.of(ExampleMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:example_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<HighExplosiveMissileEntity>> HE_MISSILE = ENTITY_TYPES.register(
            "he_missile",
            () -> EntityType.Builder.of(HighExplosiveMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:he_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<NuclearMissileEntity>> NUCLEAR_MISSILE = ENTITY_TYPES.register(
            "nuclear_missile",
            () -> EntityType.Builder.of(NuclearMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:nuclear_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<LeadBulletEntity>> LEAD_BULLET = ENTITY_TYPES.register(
            "lead_bullet",
            () -> EntityType.Builder.of(LeadBulletEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(5)
                    .build("bmnw:lead_bullet")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<MeteoriteEntity>> METEORITE = ENTITY_TYPES.register(
            "meteorite",
            () -> EntityType.Builder.<MeteoriteEntity>of(((entityType, level) -> new MeteoriteEntity(entityType, level)), MobCategory.MISC)
                    .sized(3, 3)
                    .clientTrackingRange(512)
                    .build("bmnw:meteorite")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
