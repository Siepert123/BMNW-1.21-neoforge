package com.siepert.bmnw.entity;

import com.siepert.bmnw.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "bmnw");

    public static final DeferredHolder<EntityType<?>, EntityType<BlockDebrisEntity>> BLOCK_DEBRIS = ENTITY_TYPES.register(
            "block_debris",
            () -> EntityType.Builder.of(BlockDebrisEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
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

    public static final DeferredHolder<EntityType<?>, EntityType<ExampleMissileEntity>> EXAMPLE_MISSILE = ENTITY_TYPES.register(
            "example_missile",
            () -> EntityType.Builder.of(ExampleMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:example_missile")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<NuclearMissileEntity>> NUCLEAR_MISSILE = ENTITY_TYPES.register(
            "nuclear_missile",
            () -> EntityType.Builder.of(NuclearMissileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(512)
                    .build("bmnw:nuclear_missile")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
